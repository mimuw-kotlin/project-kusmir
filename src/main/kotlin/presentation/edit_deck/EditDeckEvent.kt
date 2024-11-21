package presentation.edit_deck

import domain.model.Deck

sealed class EditDeckEvent {
    data class AddCard(val cardName: String, val type: Deck.ListType? = null): EditDeckEvent()
    data class RemoveCard(val cardName: String, val type: Deck.ListType): EditDeckEvent()
    data class EnteredDeckName(val name: String): EditDeckEvent()
    data class CardSearch(val query: String): EditDeckEvent()
    data object SaveDeck: EditDeckEvent()
    data object ToggleAddCardMenu: EditDeckEvent()
    data class SelectTargetDeckListType(val id: Int): EditDeckEvent()
}