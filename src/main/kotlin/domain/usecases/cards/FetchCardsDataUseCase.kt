package domain.usecases.cards

import domain.repository.CardsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FetchCardsDataUseCase(
    private val repository: CardsRepository,
) {
    operator fun invoke(): Flow<Float> = repository.fetchAndUpdateCardsData()
}
