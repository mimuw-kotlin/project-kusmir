package presentation.edit_deck

import domain.model.Card

data class EditDeckState(
    val deckId: Long = -1,
    val name: String = "",
    val imageUrl: String = "",
    val mainDeckCardCountMap: MutableMap<Card, Int> = mutableMapOf(),
    val sideboardCardCountMap: MutableMap<Card, Int> = mutableMapOf(),
    val isAddCardMenuExpanded: Boolean = false,
    val cardsSearchResults: List<String> = emptyList(),
    val cardsSearchQuery: String = "",
    val selectedDeckTypeId: Int = 0
)
