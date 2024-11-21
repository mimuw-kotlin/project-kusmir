package domain.use_cases.cards

import domain.model.Card
import domain.repository.CardsRepository

class GetCardByNameUseCase(
    private val repository: CardsRepository
) {
    suspend operator fun invoke(name: String): Card? {
        return repository.getCardByName(name)
    }
}