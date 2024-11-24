package presentation.edit_deck

import domain.model.Deck

sealed class EditDeckEvent {
    data class AddCard(val cardName: String, val type: Deck.ListType? = null): EditDeckEvent()
    data class RemoveCard(val cardName: String, val type: Deck.ListType): EditDeckEvent()
    data object SaveDeck: EditDeckEvent()
    data object DeleteDeck: EditDeckEvent()
    data class EnteredDeckName(val name: String): EditDeckEvent()
    data class ChangeDeckImage(val cardName: String): EditDeckEvent()

    sealed class AddCardEvent {
        data class CardSearch(val query: String): AddCardEvent()
        data object ToggleAddCardMenu: AddCardEvent()
        data class SelectTargetDeckListType(val id: Int): AddCardEvent()
    }

    sealed class ImportDeckEvent {
        data object ToggleImportPopup: ImportDeckEvent()
        data class EnteredDeckImportValue(val input: String): ImportDeckEvent()
        data object ImportDeck: ImportDeckEvent()
    }

    sealed class ChooseImageEvent {
        data class ImageSearch(val query: String): ChooseImageEvent()
        data object ToggleChooseImagePopup: ChooseImageEvent()
    }
}