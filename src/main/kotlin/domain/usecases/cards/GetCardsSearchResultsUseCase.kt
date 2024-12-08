package domain.usecases.cards

import domain.repository.CardsRepository

class GetCardsSearchResultsUseCase(
    private val cardsRepository: CardsRepository,
) {
    suspend operator fun invoke(query: String): List<String> =
        if (query.isNotBlank()) {
            cardsRepository.getCardsSearchResults(query)
        } else {
            emptyList()
        }
}
