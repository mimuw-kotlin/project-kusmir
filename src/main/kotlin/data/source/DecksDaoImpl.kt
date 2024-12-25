package data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.local.database.CardDb
import data.local.database.Database
import data.local.database.DeckDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class DecksDaoImpl(
    private val db: Database,
) : DecksDao {
    private val queries = db.databaseQueries

    override suspend fun createDeck(name: String): Long =
        withContext(Dispatchers.IO) {
            queries.transactionWithResult {
                queries.insertDeck(name, null)
                queries.getLastInsertedDeckId().executeAsOne()
            }
        }

    override suspend fun deleteDeckById(deckId: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteDeckById(deckId)
        }
    }

    override suspend fun deleteDeckByName(name: String) {
        withContext(Dispatchers.IO) {
            queries.deleteDeckByName(name)
        }
    }

    override fun getAllDecks(): Flow<List<DeckDb>> = queries.getAllDecks().asFlow().mapToList(Dispatchers.IO)

    override suspend fun getDeckById(id: Long): DeckDb? =
        withContext(Dispatchers.IO) {
            queries.getDeckById(id).executeAsOneOrNull()
        }

    override suspend fun getDeckByName(name: String): DeckDb? =
        withContext(Dispatchers.IO) {
            queries.getDeckByName(name).executeAsOneOrNull()
        }

    override suspend fun insertCardIntoMainDeck(
        cardId: Uuid,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            queries.insertCardIntoDeck(cardId, deckId, type = "main")
        }
    }

    override suspend fun insertCardIntoSideboard(
        cardId: Uuid,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            queries.insertCardIntoDeck(cardId, deckId, type = "side")
        }
    }

    override suspend fun insertCardsIntoMainDeck(
        cardIds: List<Uuid>,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            db.transaction {
                cardIds.forEach { cardId ->
                    queries.insertCardIntoDeck(cardId, deckId, type = "main")
                }
            }
        }
    }

    override suspend fun insertCardsIntoSideboard(
        cardIds: List<Uuid>,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            db.transaction {
                cardIds.forEach { cardId ->
                    queries.insertCardIntoDeck(cardId, deckId, type = "side")
                }
            }
        }
    }

    override suspend fun getCardsFromMainDeck(deckId: Long): List<CardDb> =
        withContext(Dispatchers.IO) {
            queries.getCardsFromDeck(deckId, "main").executeAsList()
        }

    override suspend fun getCardsFromSideboard(deckId: Long): List<CardDb> =
        withContext(Dispatchers.IO) {
            queries.getCardsFromDeck(deckId, "side").executeAsList()
        }

    override suspend fun deleteCardFromMainDeck(
        cardId: Uuid,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            queries.deleteCardFromDeck(cardId, deckId, type = "main")
        }
    }

    override suspend fun deleteCardFromSideboard(
        cardId: Uuid,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            queries.deleteCardFromDeck(cardId, deckId, type = "side")
        }
    }

    override suspend fun deleteCardsFromMainDeck(
        cardIds: List<Uuid>,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            db.transaction {
                cardIds.forEach { cardId ->
                    queries.deleteCardFromDeck(cardId, deckId, type = "main")
                }
            }
        }
    }

    override suspend fun deleteCardsFromSideboard(
        cardIds: List<Uuid>,
        deckId: Long,
    ) {
        withContext(Dispatchers.IO) {
            db.transaction {
                cardIds.forEach { cardId ->
                    queries.deleteCardFromDeck(cardId, deckId, type = "side")
                }
            }
        }
    }

    override suspend fun deleteAllCardsFromDeck(deckId: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteAllCardsFromDeck(deckId)
        }
    }

    override suspend fun updateDeckName(
        deckId: Long,
        name: String,
    ) {
        withContext(Dispatchers.IO) {
            queries.updateDeckName(deckId = deckId, name = name)
        }
    }

    override suspend fun updateDeckImage(
        deckId: Long,
        imageSource: String?,
    ) {
        println("inserting: $imageSource")
        withContext(Dispatchers.IO) {
            queries.updateDeckImage(deckId = deckId, imageSource = imageSource)
        }
    }
}
