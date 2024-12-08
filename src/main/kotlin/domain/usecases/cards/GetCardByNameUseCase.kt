package domain.usecases.cards

import domain.model.Card
import domain.repository.CardsRepository

class GetCardByNameUseCase(
    private val repository: CardsRepository,
) {
    suspend operator fun invoke(name: String): Card? = repository.getCardByName(name)
}
