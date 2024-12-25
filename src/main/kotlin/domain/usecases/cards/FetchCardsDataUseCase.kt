package domain.usecases.cards

import domain.repository.CardsRepository

class FetchCardsDataUseCase(
    private val repository: CardsRepository,
) {
    suspend operator fun invoke() {
        repository.fetchAndUpdateCardsData()
    }
}
