package domain.repository

import domain.model.Card
import kotlin.uuid.Uuid

interface CardsRepository {
    suspend fun getCardById(id: Uuid): Card?
    suspend fun getCardByName(name: String): Card?
    suspend fun fetchAndUpdateCardsData()
    suspend fun getCardsSearchResults(query: String, limit: Long = 10): List<String>
}
