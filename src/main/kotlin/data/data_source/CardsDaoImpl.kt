package data.data_source

import data.local.database.CardDb
import data.local.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class CardsDaoImpl(
    val db: Database
): CardsDao {

    private val queries = db.databaseQueries

    override suspend fun getById(id: Uuid): CardDb? {
        return withContext(Dispatchers.IO) {
            queries.getById(id).executeAsOneOrNull()
        }
    }

    override suspend fun getByName(name: String): CardDb? {
        return withContext(Dispatchers.IO) {
            queries.getByName(name).executeAsOneOrNull()
        }
    }

    override suspend fun deleteWithId(id: Uuid) {
        withContext(Dispatchers.IO) {
            queries.deleteWithId(id)
        }
    }

    override suspend fun deleteWithIds(ids: List<Uuid>) {
        withContext(Dispatchers.IO) {
            queries.deleteWithIds(ids)
        }
    }

    override suspend fun insert(
        id: Uuid,
        name: String,
        colors: List<String>?,
        legalities: Map<String, Boolean>,
        imageSource: String
    ) {
        withContext(Dispatchers.IO) {
            queries.insertCard(
                id,
                name,
                colors,
                legalities,
                imageSource
            )
        }
    }

    override suspend fun insert(card: CardDb) {
        withContext(Dispatchers.IO) {
            with(card) {
                queries.insertCard(
                    id,
                    name,
                    colors,
                    legalities,
                    imageSource
                )
            }
        }
    }

    override suspend fun insertMultiple(cards: List<CardDb>) {
        withContext(Dispatchers.IO) {
            db.transaction {
                cards.forEach { card ->
                    queries.insertCard(
                        card.id,
                        card.name,
                        card.colors,
                        card.legalities,
                        card.imageSource
                    )
                }
            }
        }
    }

}