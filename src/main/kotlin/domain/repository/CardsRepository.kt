package domain.repository

import domain.model.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

interface CardsRepository {
    suspend fun getCardById(id: Uuid): Card?

    suspend fun getCardByMtgoId(mtgoId: Long): Card?

    suspend fun getCardByName(name: String): Card?

    /**
     *  @return StateFlow indicating download progress status.
     */
    fun fetchAndUpdateCardsData(): Flow<Float>

    suspend fun getCardsSearchResults(
        query: String,
        limit: Long = 10,
    ): List<String>

    suspend fun getLastFetchInstant(): Instant?
}
