package presentation.decks

import domain.model.Deck

data class DecksState(
    val decks: List<Deck> = emptyList()
)