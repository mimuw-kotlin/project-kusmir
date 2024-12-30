package presentation.decktracker

import domain.model.Card
import domain.model.DeckList
import domain.usecases.tracking.ReadLogUseCase
import domain.usecases.tracking.TrackingUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import domain.usecases.tracking.ReadLogUseCase.Companion.Event.GameFinished as GameFinishedEvent
import domain.usecases.tracking.ReadLogUseCase.Companion.Event.GameStarted as GameStartedEvent
import domain.usecases.tracking.ReadLogUseCase.Companion.Event.GameStateUpdate as GameStateUpdateEvent
import domain.usecases.tracking.ReadLogUseCase.Companion.Event.MatchFinished as MatchFinishedEvent
import domain.usecases.tracking.ReadLogUseCase.Companion.Event.Skipped as SkippedEvent

class DeckTrackerViewModel(
    private val trackingUseCases: TrackingUseCases,
) {
    private val _state = MutableStateFlow(DeckTrackerState())
    val state: StateFlow<DeckTrackerState> = _state

    private var logJob: Job? = null

    init {
        val logFile = trackingUseCases.getLogFileUseCase()
        logJob =
            trackingUseCases
                .readLogUseCase(logFile) {
                    processLogEvent(it)
                }
    }

    private fun processLogEvent(event: ReadLogUseCase.Companion.Event) {
        when (event) {
            is GameFinishedEvent -> {
                // TODO: Persist seen cards
                _state.value =
                    state.value.copy(
                        isWindowOpen = false,
                        seenPlayerCards = emptyList(),
                        seenOpponentCards = emptyList(),
                    )
            }

            is GameStartedEvent -> {
                val deck = state.value.registeredDeck ?: event.registeredDeck
                _state.value =
                    DeckTrackerState(
                        isWindowOpen = true,
                        registeredDeck = deck,
                    )
            }

            is GameStateUpdateEvent -> {
                _state.value =
                    state.value.copy(
                        seenPlayerCards = event.myCards,
                        seenOpponentCards = event.opponentCards,
                        cardsLeftInDeck = updateCardsLeftInDeck(event.myCards),
                    )
            }

            is SkippedEvent -> {}

            is MatchFinishedEvent -> {
                // TODO: persist the result
                _state.value = DeckTrackerState(isWindowOpen = false)
            }
        }
    }

    fun updateCardsLeftInDeck(seenCards: List<Card>): DeckList {
        val cardsLeftInDeck =
            state.value.registeredDeck
                ?.mainDeck
                ?.toMutableDeckList()
        println("Seen cards: $seenCards")
        seenCards.forEach { card ->
            cardsLeftInDeck?.removeCard(card)
        }

        return cardsLeftInDeck?.toDeckList() ?: DeckList()
    }
}
