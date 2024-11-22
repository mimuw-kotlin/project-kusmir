package presentation.edit_deck

import domain.model.DeckList

data class SearchBoxState(
    val query: String = "",
    val results: List<String> = emptyList()
)

data class AddCardMenuState(
    val searchBoxState: SearchBoxState = SearchBoxState(),
    val isExpanded: Boolean = false,
    val selectedDeckTypeId: Int = 0
)

data class ChooseImageState(
    val searchBoxState: SearchBoxState = SearchBoxState(),
    val isVisible: Boolean = false,
)

data class ImportDeckState(
    val isVisible: Boolean = false,
    val input: String = "",
)

data class EditDeckState(
    val deckId: Long = -1,
    val deckName: String = "",
    val imageUrl: String = "",
    val mainDeck: DeckList = DeckList(),
    val sideboard: DeckList = DeckList(),

    val addCardMenuState: AddCardMenuState = AddCardMenuState(),
    val chooseImageState: ChooseImageState = ChooseImageState(),
    val importDeckState: ImportDeckState = ImportDeckState(),
)
