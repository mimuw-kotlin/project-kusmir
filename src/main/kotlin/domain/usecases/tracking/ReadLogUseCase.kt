package domain.usecases.tracking

import app.softwork.uuid.toUuid
import data.repository.util.toMtgFormat
import domain.model.Card
import domain.model.Deck
import domain.model.DeckList
import domain.model.MtgFormat
import domain.model.MutableDeckList
import domain.repository.CardsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.RandomAccessFile
import kotlin.uuid.Uuid


class LogParser(
    private val cardsRepository: CardsRepository,
) {
    sealed class Event {
        data class MatchStarted(
            val matchId: Long,
            val matchToken: Uuid,
            val format: MtgFormat,
        ): Event()

        data class GameStarted(
            val gameId: Long,
            val playerName: String,
            val registeredDeck: Deck,
        ) : Event()

        data class GameStateUpdate(
            val gameId: Long,
            val matchId: Long,
            val eventId: Long,
            val playersNames: List<String>,
            val cards: Map<String, List<Card>>,
            val sideboard: DeckList,
        ) : Event()

        data class MatchFinished(
            val token: Uuid,
        ) : Event()

        data object BeginSideboarding : Event()

        data object Skipped : Event()
    }

    @Serializable
    data class DecklistCard(
        @SerialName("CatalogId") val mtgoId: Long,
        @SerialName("Quantity") val quantity: Int,
        @SerialName("Annotation") val annotation: String,
        @SerialName("InSideboard") val inSideboard: Boolean,
    )

    @Serializable
    data class GameStateCard(
        @SerialName("Id") val gameId: Int,
        @SerialName("CatalogID") val mtgoId: Long,
        @SerialName("Zone") val zone: String,
        @SerialName("ActualZone") val actualZone: String,
        @SerialName("Owner") val owner: Int,
        @SerialName("Controller") val controller: Int,
    )

    @Serializable
    data class PlayerInfo(
        @SerialName("Id") val id: Int,
        @SerialName("Name") val name: String,
        @SerialName("LibraryCount") val libraryCount: Int,
        @SerialName("HandCount") val handCount: Int,
        @SerialName("Life") val life: Int,
    )

    @Serializable
    data class GameState(
        @SerialName("Players") val players: List<PlayerInfo>,
        @SerialName("Cards") val cards: List<GameStateCard>
    )

    private suspend fun processGameStarted(message: String): Event.GameStarted {
        val headerInfoRegex = "Username: (?<playerName>\\S+) Deck Used in Game ID: (?<gameId>\\d+)".toRegex()
        val headerInfo = headerInfoRegex.find(message)

        val gameId = headerInfo?.groups?.get("gameId")?.value?.toLong()
            ?: error("Invalid GameStarted message syntax in:\n$message")

        val playerName = headerInfo.groups.get("playerName")?.value
            ?: error("Invalid GameStarted message syntax in:\n$message")

        val mainDeck = MutableDeckList()
        val sideboard = MutableDeckList()

        // TODO: I don't know how to do it better
        val jsonStartIndex = message.indexOf(")") + 2
        val jsonSubstring = message.substring(jsonStartIndex)

        val catalogCardsList = Json.decodeFromString<List<DecklistCard>>(jsonSubstring)
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

        return Event.GameStarted(
            gameId = gameId,
            playerName = playerName,
            Deck(
                mainDeck = mainDeck.toDeckList(),
                sideboard = sideboard.toDeckList(),
            ),
        )
    }

    private suspend fun processGameSateUpdate(message: String): Event.GameStateUpdate {
        val headerInfoRegex = """Game ID: (\d+), Match ID: (\d+), Event ID: (\d+)""".toRegex()

        val (gameId, matchId, eventId) =
            headerInfoRegex.find(message)!!.destructured
                .toList()
                .map { it.toLong() }

        val jsonStartIndex = message.indexOf("{")
        val jsonSubstring = message.substring(jsonStartIndex)

        val gameState = Json.decodeFromString<GameState>(jsonSubstring)

        val (mainCards, sideboardCards) = gameState.cards.partition { it.zone != "Sideboard" }

        val seenCards = mainCards
            .groupBy { it.owner }
            .mapKeys { (id, _) ->
                gameState.players.find { it.id == id }!!.name
            }
            .mapValues { (_, cards) ->
                cards
                    .mapNotNull { cardsRepository.getCardByMtgoId(it.mtgoId) }
            }

        // We don't group sideboard cards, because only player sideboard i visible anyway.
        val sideboard = sideboardCards
            .mapNotNull { cardsRepository.getCardByMtgoId(it.mtgoId) }

        val playersNames = gameState.players.map { it.name }

        return Event.GameStateUpdate(
            gameId = gameId,
            matchId = matchId,
            eventId = eventId,
            playersNames = playersNames,
            cards = seenCards,
            sideboard = DeckList(sideboard),
        )
    }

    private fun processMatchStarted(message: String): Event.MatchStarted {
        val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()
        val matchTokenRegex = "Match Token:(?<matchToken>$uuidRegex)".toRegex()
        val matchToken = matchTokenRegex.find(message)?.groups?.get("matchToken")?.value?.toUuid()
            ?: error("Invalid MatchStarted message syntax in:\n$message")

        val matchIdRegex = "Match Id:(?<matchId>\\d+)".toRegex()
        val matchId = matchIdRegex.find(message)?.groups?.get("matchId")?.value?.toLong()
            ?: error("Invalid MatchStarted message syntax in:\n$message")

        val matchFormatRegex = "GameStructureCd= (?<matchFormat>\\S+)".toRegex()
        val matchFormat = matchFormatRegex.find(message)?.groups?.get("matchFormat")?.value?.toMtgFormat()
            ?: error("Invalid MatchStarted message syntax in:\n$message")

        return Event.MatchStarted(
            matchId = matchId,
            matchToken = matchToken,
            format = matchFormat,
        )
    }

    private fun processMatchFinished(message: String): Event.MatchFinished {
        val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()
        val token = uuidRegex.find(message)?.value?.toUuid()
            ?: error("Invalid GameStarted message syntax in:\n$message")

        return Event.MatchFinished(
            token = token,
        )
    }

    suspend fun parse(message: String): Event =
        when {
            message.contains("to MatchCompletedState") ->
                processMatchFinished(message)

            message.contains("Game Play Status Update") ->
                processGameSateUpdate(message)

            message.contains("Deck Used in Game") ->
                processGameStarted(message)

            message.contains("UI|Initialize Match") ->
                processMatchStarted(message)

            message.contains("UI|Begin Sideboarding Phase") ->
                Event.BeginSideboarding

            else -> Event.Skipped
        }
}

private class FileScanner(
    private val file: File,
) {
    private val newMessageRegex = "^\\d{2}:\\d{2}:\\d{2}.*".toRegex()

    fun scan(): Flow<String> =
        flow {
            require(file.exists()) { "File does not exist." }

            var buffer = StringBuilder()

            RandomAccessFile(file, "r").use { f ->
                f.seek(f.length()) // Start reading at the end of the file

                while (true) {
                    val newBytes = (f.length() - f.filePointer).toInt()
                    if (newBytes == 0) {
                        delay(100)
                        continue
                    }

                    val byteBuffer = ByteArray(newBytes)
                    f.readFully(byteBuffer)
                    buffer.append(String(byteBuffer))

                    val iterator = buffer.lines().iterator()
                    val currentMessage = StringBuilder()

                    while (iterator.hasNext()) {
                        val line = iterator.next()

                        if (newMessageRegex.matches(line) && currentMessage.isNotEmpty()) {
                            // A new message has started, so we emit the previous message.
                            // It is not ideal, but detecting end of message is hard,
                            // and the messages come in pretty often, so it should not be a big issue.
                            emit(currentMessage.toString().trim())
                            currentMessage.clear()
                        }

                        currentMessage.appendLine(line)
                    }

                    buffer = StringBuilder(currentMessage.toString())
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
    operator fun invoke(): Flow<LogParser.Event> {
        val parser = LogParser(cardsRepository)
        val scanner =
            FileScanner(
                file = getLogFile(),
            )

        return scanner.scan().map { parser.parse(it) }
    }
}
