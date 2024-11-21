package presentation.decks

sealed class DecksEvent {
    data object AddNewDeck: DecksEvent()
    data class SaveNewDeck(val newDeckName: String): DecksEvent()
}