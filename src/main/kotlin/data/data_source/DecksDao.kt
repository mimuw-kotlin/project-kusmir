package data.data_source

import data.local.database.CardDb
import data.local.database.DeckDb
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface DecksDao {
    suspend fun createDeck(name: String)
    suspend fun getLastInsertedDeckId(): Long
    suspend fun deleteDeckById(deckId: Long)
    suspend fun deleteDeckByName(name: String)

    fun getAllDecks(): Flow<List<DeckDb>>
    suspend fun getDeckById(id: Long): DeckDb?
    suspend fun getDeckByName(name: String): DeckDb?

    suspend fun insertCardIntoMainDeck(cardId: Uuid, deckId: Long)
    suspend fun insertCardIntoSideboard(cardId: Uuid, deckId: Long)
    suspend fun insertCardsIntoMainDeck(cardIds: List<Uuid>, deckId: Long)
    suspend fun insertCardsIntoSideboard(cardIds: List<Uuid>, deckId: Long)

    suspend fun getCardsFromMainDeck(deckId: Long): List<CardDb>
    suspend fun getCardsFromSideboard(deckId: Long): List<CardDb>

    suspend fun deleteCardFromMainDeck(cardId: Uuid, deckId: Long)
    suspend fun deleteCardFromSideboard(cardId: Uuid, deckId: Long)
    suspend fun deleteCardsFromMainDeck(cardIds: List<Uuid>, deckId: Long)
    suspend fun deleteCardsFromSideboard(cardIds: List<Uuid>, deckId: Long)
    suspend fun deleteAllCardsFromDeck(deckId: Long)

    suspend fun updateDeckName(deckId: Long, name: String)
}