package domain.repository

import domain.model.Deck

interface DecksRepository {
    suspend fun createDeck(name: String)
    suspend fun saveDeck(deck: Deck)
    suspend fun fetchDeckById(id: Long): Deck?
}