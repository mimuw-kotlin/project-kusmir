package data.repository

import data.data_source.DecksDao
import data.local.database.DeckDb
import data.repository.util.toDomain
import domain.model.Card
import domain.model.Deck
import domain.model.DeckList
import domain.repository.DecksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckRepositoryImpl(
    private val dao: DecksDao
): DecksRepository {
    private suspend fun DeckDb.toDomain(): Deck =
        Deck(
            id = this.id,
            name = this.name,
            mainDeck = DeckList(dao.getCardsFromMainDeck(this.id).map { it.toDomain() }),
            sideboard = DeckList(dao.getCardsFromSideboard(this.id).map { it.toDomain() })
        )

    override suspend fun createDeck(name: String, mainDeck: DeckList, sideboard: DeckList): Long {
        val deckId = dao.createDeck(name)

        dao.insertCardsIntoMainDeck(
            mainDeck.toCardsList().map { it.id }, deckId
        )

        dao.insertCardsIntoSideboard(
            sideboard.toCardsList().map { it.id }, deckId
        )

        return deckId
    }

    override suspend fun deleteDeckById(id: Long) =
        dao.deleteDeckById(id)

    override suspend fun deleteDeckByName(name: String) =
        dao.deleteDeckByName(name)

    override suspend fun saveDeck(deck: Deck) {
        dao.updateDeckName(deck.id, deck.name)

        dao.deleteAllCardsFromDeck(deck.id)

        dao.insertCardsIntoMainDeck(
            deck.mainDeck.toCardsList().map { it.id }, deck.id
        )

        dao.insertCardsIntoSideboard(
            deck.sideboard.toCardsList().map { it.id }, deck.id
        )
    }

    override suspend fun fetchDeckById(id: Long): Deck? =
        dao.getDeckById(id)?.toDomain()

    override fun fetchAllDecks(): Flow<List<Deck>> =
        dao.getAllDecks().map { decksList ->
            decksList.map {
                deck -> deck.toDomain()
            }
        }

}