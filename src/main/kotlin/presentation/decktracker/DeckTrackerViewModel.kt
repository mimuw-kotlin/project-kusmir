package presentation.decktracker

import domain.model.Card
import domain.model.Deck
import domain.model.DeckList
import domain.model.GameReport
import domain.model.GameResult
import domain.model.MatchReport
import domain.model.MtgFormat
import domain.usecases.tracking.ParsedGameResult
import domain.usecases.tracking.ReadLogEvent
import domain.usecases.tracking.TrackingUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

private data class GameInfo(
    val startingDeck: Deck,
    val playerCards: List<Card> = emptyList(),
    val opponentCards: List<Card> = emptyList(),
)

private data class MatchInfo(
    var playerName: String? = null,
    var opponentName: String? = null,
    var format: MtgFormat? = null,
    var registeredDeck: Deck? = null,
)

class DeckTrackerViewModel(
    private val trackingUseCases: TrackingUseCases,
) {
    private val _state = MutableStateFlow(DeckTrackerState())
    val state: StateFlow<DeckTrackerState> = _state

    private val logReader = Job()
    private val scope = CoroutineScope(Dispatchers.Default + logReader)

    private val gamesInfo: MutableList<GameInfo> = mutableListOf()
    private val matchInfo = MatchInfo()

    init {
        val logEventFlow = trackingUseCases.readLog()
        scope.launch {
            logEventFlow.collect {
                processLogEvent(it)
            }
        }
    }

    private fun processLogEvent(event: ReadLogEvent) {
        println(event)
        when (event) {
            is ReadLogEvent.GameStarted -> {
                gamesInfo.add(GameInfo(startingDeck = event.registeredDeck))
                if (matchInfo.registeredDeck == null)
                    matchInfo.registeredDeck = event.registeredDeck

                _state.value =
                    state.value.copy(
                        isWindowOpen = true,
                        currentDeck = event.registeredDeck,
                    )
            }

            is ReadLogEvent.GameStateUpdate -> {
                require(gamesInfo.isNotEmpty())

                if (matchInfo.playerName == null) {
                    matchInfo.playerName = event.playerName
                    matchInfo.opponentName = event.opponentName
                }

                gamesInfo[gamesInfo.lastIndex] =
                    gamesInfo[gamesInfo.lastIndex].copy(
                        playerCards = event.playerCards,
                        opponentCards = event.opponentCards,
                    )

                _state.value =
                    state.value.copy(
                        cardsLeftInDeck = updateCardsLeftInDeck(event.playerCards),
                    )
            }

            is ReadLogEvent.Skipped -> {}

            is ReadLogEvent.MatchFinished -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val gameResults = trackingUseCases.parseMatchLog()
                    val matchReport = prepareMatchReport(gameResults)
                    //TODO: persist matchReport
                }
                _state.value = DeckTrackerState(isWindowOpen = false)
            }
        }
    }

    private fun updateCardsLeftInDeck(seenCards: List<Card>): DeckList {
        val cardsLeftInDeck =
            matchInfo.registeredDeck
                ?.mainDeck
                ?.toMutableDeckList()

        seenCards.forEach { card ->
            cardsLeftInDeck?.removeCard(card)
        }

        return cardsLeftInDeck?.toDeckList() ?: DeckList()
    }

    private fun prepareMatchReport(
        parsedGameResults: List<ParsedGameResult>
    ): MatchReport {
        require(parsedGameResults.size == gamesInfo.size)

        val gameReports =
            (parsedGameResults zip gamesInfo).map { (result, gameInfo) ->
                GameReport(
                    playerStartingDeck = gameInfo.startingDeck,
                    result = if (result.winner == matchInfo.playerName) GameResult.WON else GameResult.LOST,
                    isOnThePlay = result.startingPlayer == matchInfo.playerName,
                    opponentRevealedCards = gameInfo.opponentCards,
                    playerDrawnCards = gameInfo.playerCards,
                    playerMulligan = result.handSizes[matchInfo.playerName]!!,
                    opponentMulligan = result.handSizes[matchInfo.opponentName]!!
                )
            }

        return MatchReport(
            opponentName = matchInfo.opponentName ?: "Unknown",
            date = Calendar.getInstance().time,
            structure = if (gameReports.size == 1) MatchReport.Structure.Bo1 else MatchReport.Structure.Bo3,
            format = matchInfo.format ?: MtgFormat.UNKNOWN,
            registeredDeck = matchInfo.registeredDeck ?: Deck.emptyDeck(),
            gameReports = gameReports
        )
    }
}
