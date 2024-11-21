package domain.use_cases.deck

data class DecksUseCases(
    val getDeck: GetDeckUseCase,
    val getAllDecks: GetAllDecksUseCase,
    val saveDeck: SaveDeckUseCase,
    val fetchAndUpdateCards: FetchAndUpdateCardsUseCase //DEBUG ONLY
)