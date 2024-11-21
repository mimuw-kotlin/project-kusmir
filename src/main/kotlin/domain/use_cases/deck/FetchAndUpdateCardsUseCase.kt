package domain.use_cases.deck

import domain.repository.CardsRepository

class FetchAndUpdateCardsUseCase(
    private val repository: CardsRepository
) {
    suspend operator fun invoke() {
        repository.fetchAndUpdateCardsData()
    }
}