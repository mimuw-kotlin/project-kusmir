package presentation.decktracker

import domain.model.Card
import domain.model.Deck
import domain.model.DeckList
import domain.model.GameReport
import domain.model.GameResult
import domain.model.MatchReport
import domain.model.MtgFormat
import domain.usecases.deck.DecksUseCases
import domain.usecases.statistics.StatisticsUseCases
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

private fun List<GameInfo>.copy() =
    this.map { it.copy() }

private data class MatchInfo(
    var playerName: String? = null,
    var opponentName: String? = null,
    var format: MtgFormat? = null,
    var registeredDeck: Deck? = null,
)

class DeckTrackerViewModel(
    private val trackingUseCases: TrackingUseCases,
    private val decksUseCases: DecksUseCases,
    private val statisticsUseCases: StatisticsUseCases
) {
    private val _state = MutableStateFlow(DeckTrackerState())
    val state: StateFlow<DeckTrackerState> = _state

    private val logReader = Job()
    private val scope = CoroutineScope(Dispatchers.Default + logReader)

    private val gamesInfo: MutableList<GameInfo> = mutableListOf()
    private var matchInfo = MatchInfo()

    init {
        val logEventFlow = trackingUseCases.readLog()
        scope.launch {
            logEventFlow.collect {
                processLogEvent(it)
            }
        }
    }

    private fun processLogEvent(event: ReadLogEvent) {
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
                val gamesInfoCopy = gamesInfo.copy()
                val matchInfoCopy = matchInfo.copy()

                CoroutineScope(Dispatchers.IO).launch {
                    val gameResults = trackingUseCases.parseMatchLog()
                    persistMatchReport(gameResults, gamesInfoCopy, matchInfoCopy)
                }
                _state.value = DeckTrackerState(isWindowOpen = false)
                gamesInfo.clear()
                matchInfo = MatchInfo()
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

    private suspend fun persistMatchReport(
        parsedGameResults: List<ParsedGameResult>,
        gamesInfo: List<GameInfo>,
        matchInfo: MatchInfo,
    ) {
        require(parsedGameResults.size == gamesInfo.size)

        val gameReports =
            (parsedGameResults zip gamesInfo).map { (result, gameInfo) ->
                val (addedCards, removedCards) =
                    decksUseCases.getSideboardingDataUseCase(
                        registeredDeck = matchInfo.registeredDeck!!,
                        currentDeck = gameInfo.startingDeck,
                    )

                GameReport(
                    result = if (result.winner == matchInfo.playerName) GameResult.WON else GameResult.LOST,
                    isOnThePlay = result.startingPlayer == matchInfo.playerName,
                    opponentRevealedCards = gameInfo.opponentCards,
                    playerDrawnCards = gameInfo.playerCards,
                    playerMulligan = result.handSizes[matchInfo.playerName] ?: 0,
                    opponentMulligan = result.handSizes[matchInfo.opponentName] ?: 0,
                    cardsSidedIn = addedCards,
                    cardsSidedOut = removedCards,
                )
            }

        val registeredDeck = decksUseCases.getMatchingDeckUseCase(
            matchInfo.registeredDeck ?: Deck.emptyDeck())

        statisticsUseCases.saveMatchReport(
            MatchReport(
                id = -1L,
                opponentName = matchInfo.opponentName ?: "Unknown",
                date = Calendar.getInstance().time,
                structure = if (gameReports.size == 1) MatchReport.Structure.Bo1 else MatchReport.Structure.Bo3,
                format = matchInfo.format ?: MtgFormat.UNKNOWN,
                registeredDeckId = registeredDeck.id,
                gameReports = gameReports
            )
        )
    }
}
