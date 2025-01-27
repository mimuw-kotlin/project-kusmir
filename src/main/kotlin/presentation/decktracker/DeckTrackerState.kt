package presentation.decktracker

import domain.model.Card
import domain.model.Deck
import domain.model.DeckList

data class DeckTrackerState(
    val isWindowOpen: Boolean = false,
    val registeredDeck: Deck? = null,
    val currentDeck: Deck? = null,
    val seenPlayerCards: List<Card> = emptyList(),
    val seenOpponentCards: List<Card> = emptyList(),
    val cardsLeftInDeck: DeckList = DeckList(),
)
