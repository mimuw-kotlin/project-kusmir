package presentation.edit_deck

import domain.model.DeckList


data class EditDeckState(
    val deckId: Long = -1,
    val deckName: String = "",
    val imageUrl: String? = null,
    val mainDeck: DeckList = DeckList(),
    val sideboard: DeckList = DeckList()
) {
    data class AddCardMenuState(
        val searchQuery: String = "",
        val searchResults: List<String> = emptyList(),
        val isExpanded: Boolean = false,
        val selectedDeckTypeId: Int = 0,
    )

    data class ChooseImageState(
        val searchQuery: String = "",
        val searchResults: List<String> = emptyList(),
        val isVisible: Boolean = false,
    )

    data class ImportDeckState(
        val input: String = "",
        val isVisible: Boolean = false,
    )
}
