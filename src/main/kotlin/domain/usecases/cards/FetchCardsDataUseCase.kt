package domain.usecases.cards

import domain.repository.CardsRepository
import kotlinx.coroutines.flow.Flow

class FetchCardsDataUseCase(
    private val repository: CardsRepository,
) {
    operator fun invoke(): Flow<Float> = repository.fetchAndUpdateCardsData()
}
