package domain.usecases.deck

import domain.model.Deck
import domain.repository.DecksRepository
import kotlinx.coroutines.flow.Flow

class GetAllDecksUseCase(
    private val repository: DecksRepository,
) {
    operator fun invoke(): Flow<List<Deck>> = repository.fetchAllDecks()
}
