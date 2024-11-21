package domain.use_cases.deck

import domain.model.Deck
import domain.repository.DecksRepository

class GetDeckUseCase(
    private val decksRepository: DecksRepository
) {
    suspend operator fun invoke(id: Long): Deck? {
        return decksRepository.fetchDeckById(id)
    }
}