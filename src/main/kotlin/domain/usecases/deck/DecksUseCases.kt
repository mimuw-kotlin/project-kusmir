package domain.usecases.deck

data class DecksUseCases(
    val getDeck: GetDeckUseCase,
    val getAllDecks: GetAllDecksUseCase,
    val saveDeck: SaveDeckUseCase,
    val importDeck: ImportDeckUseCase,
    val deleteDeckUseCase: DeleteDeckUseCase,
    // DEBUG ONLY
    val fetchAndUpdateCards: FetchAndUpdateCardsUseCase,
)
