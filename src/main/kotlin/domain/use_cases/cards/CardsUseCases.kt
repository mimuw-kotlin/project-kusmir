package domain.use_cases.cards

data class CardsUseCases(
    val getSearchResults: GetCardsSearchResultsUseCase,
    val getCardByName: GetCardByNameUseCase
)