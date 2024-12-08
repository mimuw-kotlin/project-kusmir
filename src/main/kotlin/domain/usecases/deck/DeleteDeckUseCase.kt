package domain.usecases.deck

import domain.repository.DecksRepository

class DeleteDeckUseCase(
    private val repository: DecksRepository,
) {
    suspend operator fun invoke(deckId: Long) {
        repository.deleteDeckById(deckId)
    }
}
