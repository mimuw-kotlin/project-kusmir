package domain.use_cases.deck

import presentation.edit_deck.EditDeckEvent

data class DecksUseCases(
    val getDeck: GetDeckUseCase,
    val getAllDecks: GetAllDecksUseCase,
    val saveDeck: SaveDeckUseCase,
    val importDeck: ImportDeckUseCase,
    val fetchAndUpdateCards: FetchAndUpdateCardsUseCase //DEBUG ONLY
)