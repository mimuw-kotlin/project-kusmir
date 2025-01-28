package domain.usecases.tracking

import domain.model.Card
import domain.model.Deck
import domain.model.MutableDeckList
import domain.repository.CardsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.RandomAccessFile
import java.lang.Thread.sleep

sealed class ReadLogEvent {
    data class GameStarted(
        val registeredDeck: Deck,
    ) : ReadLogEvent()

    data class GameStateUpdate(
        val playerName: String,
        val opponentName: String,
        val playerCards: List<Card>,
        val opponentCards: List<Card>,
    ) : ReadLogEvent()

    data object MatchFinished : ReadLogEvent()

    data object Skipped : ReadLogEvent()
}

private class LogParser(
    private val cardsRepository: CardsRepository,
) {
    @Serializable
    private data class DecklistCardItem(
        @SerialName("CatalogId") val mtgoId: Long,
        @SerialName("Quantity") val quantity: Int,
        @SerialName("Annotation") val annotation: String,
        @SerialName("InSideboard") val inSideboard: Boolean,
    )

    @Serializable
    private data class GameStateCardItem(
        @SerialName("Id") val gameId: Int,
        @SerialName("CatalogID") val mtgoId: Long,
        @SerialName("Zone") val zone: String,
        @SerialName("ActualZone") val actualZone: String,
        @SerialName("Owner") val owner: Int,
        @SerialName("Controller") val controller: Int,
    )

    @Serializable
    private data class PlayerInfo(
        @SerialName("Id") val id: Int,
        @SerialName("Name") val name: String,
        @SerialName("LibraryCount") val libraryCount: Int,
        @SerialName("HandCount") val handCount: Int,
        @SerialName("Life") val life: Int,
    )

    @Serializable
    private data class GameState(
        @SerialName("Players") val players: List<PlayerInfo>,
        @SerialName("Cards") val cards: List<GameStateCardItem>
    )

    private suspend fun processGameStarted(message: String): ReadLogEvent.GameStarted {
        val mainDeck = MutableDeckList()
        val sideboard = MutableDeckList()

        // TODO: I don't know how to do it better
        val jsonStartIndex = message.indexOf(")") + 2
        val jsonSubstring = message.substring(jsonStartIndex)

        val catalogCardsList = Json.decodeFromString<List<DecklistCardItem>>(jsonSubstring)
        catalogCardsList.forEach { decklistCardItem ->
            val card = cardsRepository.getCardByMtgoId(decklistCardItem.mtgoId)
            if (card == null) {
                println("Error: Card with mtgoId ${decklistCardItem.mtgoId} not found.")
                return@forEach
            }

            if (decklistCardItem.inSideboard) {
                sideboard.addCard(card, decklistCardItem.quantity)
            } else {
                mainDeck.addCard(card, decklistCardItem.quantity)
            }
        }

        return ReadLogEvent.GameStarted(
            Deck(
                mainDeck = mainDeck.toDeckList(),
                sideboard = sideboard.toDeckList(),
            ),
        )
    }

    private suspend fun processGameSateUpdate(message: String): ReadLogEvent.GameStateUpdate {
        val jsonStartIndex = message.indexOf("{")
        val jsonSubstring = message.substring(jsonStartIndex)

        val gameState = Json.decodeFromString<GameState>(jsonSubstring)

        val seenCards =
            gameState.cards
                .groupBy { it.owner }
                .mapValues { (_, cards) ->
                    cards
                        .filter { it.zone != "Sideboard" }
                        .mapNotNull {
                            cardsRepository.getCardByMtgoId(it.mtgoId)
                        }
                }

        return ReadLogEvent.GameStateUpdate(
            playerName = gameState.players[0].name,
            opponentName = if (gameState.players.size >= 2) gameState.players[1].name else "",
            playerCards = seenCards[0].orEmpty(),
            opponentCards = seenCards[1].orEmpty(),
        )
    }

    suspend fun parse(message: String): ReadLogEvent =
        when {
            message.contains("GsCloseMatchMessage") ->
                ReadLogEvent.MatchFinished

            message.contains("Game Play Status Update") ->
                processGameSateUpdate(message)

            message.contains("Deck Used to Join Event") ->
                processGameStarted(message)

            else -> ReadLogEvent.Skipped
        }
}

private class FileScanner(
    private val file: File,
    private val separator: String,
) {
    fun scan(): Flow<String> =
        flow {
            require(file.exists()) { "File does not exist." }

            var buffer = StringBuilder()

            RandomAccessFile(file, "r").use { f ->
                f.seek(f.length()) // Start reading at the end of the file

                while (true) {
                    val newBytes = (f.length() - f.filePointer).toInt()
                    if (newBytes == 0) {
                        sleep(100)
                        continue
                    }

                    val byteBuffer = ByteArray(newBytes)
                    f.readFully(byteBuffer)
                    buffer.append(String(byteBuffer))

                    buffer.split(separator)
                        .forEachIndexed { index, message ->
                            // Emit only complete messages (except for the last one which may be incomplete)
                            if (message.isNotEmpty() && (index < buffer.split(separator).lastIndex || buffer.endsWith(separator))) {
                                emit(message.trim())
                            }
                        }

                    buffer = StringBuilder(buffer.toString().substringAfterLast(separator))
                }
            }
        }.flowOn(Dispatchers.IO)
}

private fun getMtgoRootDirectory(): File {
    val userName = System.getProperty("user.name")
    return File("C:\\Users\\$userName\\AppData\\Local\\Apps\\2.0")
}

private fun getLogFile(): File {
    val root = getMtgoRootDirectory()

    /*
        I don't really understand where the logs are stored.
        According to personal tests, the path patterns follow:
            [mtgoRootDirectory]\
                [some random letters]\
                    [more random letters]\
                        mtgo..tion_[a lot of random characters]\
                            Logs\
                                mtgo

         Sometimes when game starts, new directory is created for the session, sometimes
         logs are appended to the file from previous session.

         How this should be resolved remains a task for the future, for now we're
         looking for the path as described above and take one that was last modified
     */

    val subdirectories =
        root
            .list()
            ?.filter { it != "Data" && File(root, it).isDirectory }
            ?.map { File(root, it) }

    val logFile =
        subdirectories
            ?.flatMap { file -> file.walk().toList() }
            ?.filter {
                it.isFile &&
                    it.name == "mtgo.log" &&
                    it.path.contains("mtgo..tion_")
            }?.maxByOrNull { it.lastModified() }!!

    return logFile
}

class ReadLogUseCase(
    private val cardsRepository: CardsRepository,
) {
    private val mainLogFileSeparator = "\n"

    operator fun invoke(): Flow<ReadLogEvent> {
        val parser = LogParser(cardsRepository)
        val scanner =
            FileScanner(
                file = getLogFile(),
                separator = mainLogFileSeparator,
            )

        return scanner.scan().map { parser.parse(it) }
    }
}
