package domain.use_cases.cards

import domain.repository.CardsRepository

class GetCardsSearchResultsUseCase(
    private val cardsRepository: CardsRepository
) {
    suspend operator fun invoke(query: String): List<String> {
        return if (query.isNotBlank()) {
            cardsRepository.getCardsSearchResults(query)
        } else {
            emptyList()
        }
    }
}