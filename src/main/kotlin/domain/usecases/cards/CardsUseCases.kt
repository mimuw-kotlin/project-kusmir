package domain.usecases.cards

data class CardsUseCases(
    val getSearchResults: GetCardsSearchResultsUseCase,
    val getCardByName: GetCardByNameUseCase,
)
