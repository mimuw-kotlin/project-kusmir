package domain.repository

import domain.model.Card
import domain.model.Deck
import domain.model.DeckList
import kotlinx.coroutines.flow.Flow

interface DecksRepository {
    suspend fun createDeck(name: String, mainDeck: DeckList, sideboard: DeckList): Long
    suspend fun deleteDeckById(id: Long)
    suspend fun deleteDeckByName(name: String)
    suspend fun saveDeck(deck: Deck)
    suspend fun fetchDeckById(id: Long): Deck?
    fun fetchAllDecks(): Flow<List<Deck>>
}