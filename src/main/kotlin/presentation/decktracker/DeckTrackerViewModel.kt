package presentation.decktracker

import domain.model.Card
import domain.model.DeckList
import domain.usecases.tracking.ReadLogEvent
import domain.usecases.tracking.TrackingUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeckTrackerViewModel(
    trackingUseCases: TrackingUseCases,
) {
    private val _state = MutableStateFlow(DeckTrackerState())
    val state: StateFlow<DeckTrackerState> = _state

    private val logReader = Job()
    private val scope = CoroutineScope(Dispatchers.Default + logReader)

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
                println("GAME STARTED")
                val deck = state.value.registeredDeck ?: event.registeredDeck
                _state.value =
                    DeckTrackerState(
                        isWindowOpen = true,
                        registeredDeck = deck,
                    )
            }

            is ReadLogEvent.GameStateUpdate -> {
                _state.value =
                    state.value.copy(
                        seenPlayerCards = event.myCards,
                        seenOpponentCards = event.opponentCards,
                        cardsLeftInDeck = updateCardsLeftInDeck(event.myCards),
                    )
            }

            is ReadLogEvent.Skipped -> {}

            is ReadLogEvent.MatchFinished -> {
                // TODO: persist the result
                _state.value = DeckTrackerState(isWindowOpen = false)
            }
        }
    }

    private fun updateCardsLeftInDeck(seenCards: List<Card>): DeckList {
        val cardsLeftInDeck =
            state.value.registeredDeck
                ?.mainDeck
                ?.toMutableDeckList()

        seenCards.forEach { card ->
            cardsLeftInDeck?.removeCard(card)
        }

        return cardsLeftInDeck?.toDeckList() ?: DeckList()
    }
}
