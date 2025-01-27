package presentation.decktracker

import domain.model.Deck
import domain.model.DeckList


data class DeckTrackerState(
    val isWindowOpen: Boolean = false,
    val currentDeck: Deck? = null,
    val cardsLeftInDeck: DeckList = DeckList(),
)
