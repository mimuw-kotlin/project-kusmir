package domain.usecases.tracking

import domain.model.Card
import domain.model.Deck
import domain.model.MutableDeckList
import domain.repository.CardsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.io.RandomAccessFile
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService

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

class ReadLogUseCase(
    private val cardsRepository: CardsRepository,
) {
    companion object {
        sealed class Event {
            data class GameStarted(
                val registeredDeck: Deck,
            ) : Event()

            data class GameStateUpdate(
                val myCards: List<Card>,
                val opponentCards: List<Card>,
            ) : Event()

            data object GameFinished : Event()

            data object MatchFinished : Event()

            data object Skipped : Event()
        }
    }

    private suspend fun processGameStarted(message: String): Event.GameStarted {
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

        return Event.GameStarted(
            Deck(
                mainDeck = mainDeck.toDeckList(),
                sideboard = sideboard.toDeckList(),
            ),
        )
    }

    private suspend fun processGameSateUpdate(message: String): Event.GameStateUpdate {
        val jsonStartIndex = message.indexOf("{")
        val jsonSubstring = message.substring(jsonStartIndex)

        val jsonObject = Json.parseToJsonElement(jsonSubstring).jsonObject
        val gameStateCards =
            jsonObject["Cards"]?.jsonArray?.let {
                Json.decodeFromJsonElement<List<GameStateCardItem>>(it)
            } ?: emptyList()

        val seenCards =
            gameStateCards
                .groupBy { it.owner }
                .mapValues { (_, cards) ->
                    cards
                        .filter { it.zone != "Sideboard" }
                        .mapNotNull {
                            cardsRepository.getCardByMtgoId(it.mtgoId)
                        }
                }

        return Event.GameStateUpdate(
            myCards = seenCards[0].orEmpty(),
            opponentCards = seenCards[1].orEmpty(),
        )
    }

    private suspend fun parseLogMessage(message: String): Event =
        when {
            message.contains("GsCloseMatchMessage") ->
                Event.MatchFinished
            message.contains("Game Play Status Update") ->
                processGameSateUpdate(message)
            message.contains("Deck Used to Join Event") ->
                processGameStarted(message)
            else -> Event.Skipped
        }.also { if (it !is Event.Skipped) println(it) }

    private fun readNewContent(
        logFile: File,
        onMessageRead: (Event) -> Unit,
        position: Long,
    ): Long {
        RandomAccessFile(logFile, "r").use { file ->
            file.seek(position) // Start from the last position
            var line: String?
            while (file.readLine().also { line = it } != null) {
                runBlocking {
                    val event = parseLogMessage(line!!)
                    onMessageRead(event)
                }
            }
            return file.filePointer
        }
    }

    operator fun invoke(
        logFile: File,
        onMessageRead: (Event) -> Unit,
    ): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            if (!logFile.exists()) {
                println("Log file does not exist!")
                return@launch
            }

            // Start reading the file from the end
            var position = logFile.length()

            // Create and register WatchService
            val watchService: WatchService = FileSystems.getDefault().newWatchService()
            Paths.get(logFile.path).parent.register(
                watchService,
                StandardWatchEventKinds.ENTRY_MODIFY,
            )

            println("Watching for changes in ${logFile.name}...")
            try {
                while (isActive) {
                    // Apparently sometimes the changes are not marked correctly
                    // and no poll event is triggered. To take care of it we additionally
                    // check the file size and look for changes manually

                    val currentLength = logFile.length()
                    if (currentLength > position) {
                        position = readNewContent(logFile, onMessageRead, position)
                    }

                    delay(100)
                }
            } catch (e: Exception) {
                println("Error monitoring log file: ${e.message}")
            } finally {
                watchService.close()
            }
        }
    }
}
