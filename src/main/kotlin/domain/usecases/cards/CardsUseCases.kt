package domain.usecases.cards

data class CardsUseCases(
    val getSearchResults: GetCardsSearchResultsUseCase,
    val getCardByName: GetCardByNameUseCase,
    val fetchCardsData: FetchCardsDataUseCase,
    val getLastFetchDateTime: GetLastFetchDateTimeUseCase,
)
