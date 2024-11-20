package data.repository

import data.data_source.DecksDao
import data.repository.util.toDomain
import domain.model.Deck
import domain.repository.DecksRepository

class DeckRepositoryImpl(
    private val dao: DecksDao
): DecksRepository {
    override suspend fun createDeck(name: String) {
        dao.createDeck(name)
    }

    override suspend fun saveDeck(deck: Deck) {
        dao.updateDeckName(deck.id, deck.name)

        dao.deleteAllCardsFromDeck(deck.id)

        dao.insertCardsIntoMainDeck(
            deck.mainDeck.map { it.id }, deck.id
        )
        dao.insertCardsIntoSideboard(
            deck.sideboard.map { it.id }, deck.id
        )
    }

    override suspend fun fetchDeckById(id: Long): Deck? {
        val deckDb = dao.getDeckById(id) ?: return null

        return Deck(
            id = deckDb.id,
            name = deckDb.name,
            mainDeck = dao.getCardsFromMainDeck(id).map { it.toDomain() },
            sideboard = dao.getCardsFromSideboard(id).map { it.toDomain() }
        )
    }

}