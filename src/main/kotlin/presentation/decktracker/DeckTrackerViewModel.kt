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
import domain.usecases.tracking.LogParser
import domain.usecases.tracking.ParsedGameResult
import domain.usecases.tracking.TrackingUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.uuid.Uuid

private data class GameInfo(
    val sideboard: DeckList? = null,
    val playerCards: List<Card> = emptyList(),
    val opponentCards: List<Card> = emptyList(),
)

private fun List<GameInfo>.copy() = this.map { it.copy() }

private data class MatchInfo(
    var matchId: Long? = null,
    var format: MtgFormat? = null,
    var opponentName: String? = null,
    var registeredDeck: Deck? = null,
)

class DeckTrackerViewModel(
    private val trackingUseCases: TrackingUseCases,
    private val decksUseCases: DecksUseCases,
    private val statisticsUseCases: StatisticsUseCases,
) {
    private val _state = MutableStateFlow(DeckTrackerState())
    val state: StateFlow<DeckTrackerState> = _state

    private val logReader = Job()
    private val scope = CoroutineScope(Dispatchers.Default + logReader)

    private var matchInfo = MatchInfo()
    private val gamesInfo: MutableList<GameInfo> = mutableListOf()

    private var currentMatchToken: Uuid? = null
    private var currentGameId: Long? = null
    private var playerName: String? = null

    init {
        val logEventFlow = trackingUseCases.readLog()
        scope.launch {
            logEventFlow.collect {
                processLogEvent(it)
            }
        }
    }

    private fun processLogEvent(event: LogParser.Event) {
        when (event) {
            is LogParser.Event.MatchStarted -> {
                /*
                    The game sends this message twice. I don't really understand why,
                    but the first match token is later overwritten by the second message.
                    Rest of the fields remain the same, so we ignore this fact and still update data.
                 */

                currentMatchToken = event.matchToken
                matchInfo.matchId = event.matchId
                matchInfo.format = event.format
            }

            is LogParser.Event.GameStarted -> {
                playerName = event.playerName

                if (currentGameId != event.gameId) {
                    // Sometimes the message is sent multiple times. We want to detect when the new
                    // game really starts.
                    currentGameId = event.gameId

                    gamesInfo.add(GameInfo())
                    if (matchInfo.registeredDeck == null) {
                        matchInfo.registeredDeck = event.registeredDeck
                    }
                }

                _state.value =
                    state.value.copy(
                        isWindowOpen = true,
                        currentDeck = event.registeredDeck,
                    )
            }

            is LogParser.Event.GameStateUpdate -> {
                if (matchInfo.opponentName == null) {
                    matchInfo.opponentName = event.playersNames.find { it != playerName }
                }

                gamesInfo[gamesInfo.lastIndex] =
                    gamesInfo[gamesInfo.lastIndex].copy(
                        playerCards = event.cards[playerName].orEmpty(),
                        opponentCards = event.cards[matchInfo.opponentName].orEmpty(),
                        sideboard = gamesInfo.last().sideboard ?: event.sideboard,
                    )

                _state.value =
                    state.value.copy(
                        cardsLeftInDeck = updateCardsLeftInDeck(event.cards[playerName].orEmpty()),
                    )
            }

            is LogParser.Event.MatchFinished -> {
                if (currentMatchToken != event.token) {
                    // Game emits those messages for all currently played games.
                    // We discard messages that are not tied to current match.
                    return
                }

                val gamesInfoCopy = gamesInfo.copy()
                val matchInfoCopy = matchInfo.copy()
                val playerNameCopy: String = playerName!!

                CoroutineScope(Dispatchers.IO).launch {
                    val gameResults = trackingUseCases.parseMatchLog()
                    persistMatchReport(
                        parsedGameResults = gameResults,
                        gamesInfo = gamesInfoCopy,
                        matchInfo = matchInfoCopy,
                        playerName = playerNameCopy,
                    )
                }
                _state.value = DeckTrackerState(isWindowOpen = false)

                gamesInfo.clear()
                matchInfo = MatchInfo()

                currentGameId = null
                currentMatchToken = null
                playerName = null
            }

            is LogParser.Event.BeginSideboarding -> {
                // What we need to know about this event is that the game has ended.
                _state.value = DeckTrackerState(isWindowOpen = false)
            }

            LogParser.Event.Skipped -> {}
        }.also {
            println(event)
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
        playerName: String,
    ) {
        require(parsedGameResults.size == gamesInfo.size)

        val gameReports =
            (parsedGameResults zip gamesInfo).map { (result, gameInfo) ->
                val (addedCards, removedCards) =
                    decksUseCases.getSideboardingDataUseCase(
                        registeredDeck = matchInfo.registeredDeck!!,
                        currentSideboard = gameInfo.sideboard ?: DeckList(),
                    )

                GameReport(
                    result = if (result.winner == playerName) GameResult.WON else GameResult.LOST,
                    isOnThePlay = result.startingPlayer == playerName,
                    opponentRevealedCards = gameInfo.opponentCards,
                    playerDrawnCards = gameInfo.playerCards,
                    playerMulligan = result.handSizes[playerName] ?: 0,
                    opponentMulligan = result.handSizes[matchInfo.opponentName] ?: 0,
                    cardsSidedIn = addedCards,
                    cardsSidedOut = removedCards,
                )
            }

        val registeredDeck =
            decksUseCases.getMatchingDeckUseCase(
                matchInfo.registeredDeck ?: Deck.emptyDeck(),
            )

        statisticsUseCases.saveMatchReport(
            MatchReport(
                id = -1L,
                opponentName = matchInfo.opponentName ?: "Unknown",
                date = Calendar.getInstance().time,
                structure = if (gameReports.size == 1) MatchReport.Structure.Bo1 else MatchReport.Structure.Bo3,
                format = matchInfo.format ?: MtgFormat.UNKNOWN,
                registeredDeckId = registeredDeck.id,
                gameReports = gameReports,
            ),
        )
    }
}
