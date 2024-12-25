package data.source

import data.local.database.CardDb
import data.local.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class CardsDaoImpl(
    val db: Database,
) : CardsDao {
    private val queries = db.databaseQueries

    override suspend fun getById(id: Uuid): CardDb? =
        withContext(Dispatchers.IO) {
            queries.getById(id).executeAsOneOrNull()
        }

    override suspend fun getByName(name: String): CardDb? =
        withContext(Dispatchers.IO) {
            queries.getByName(name).executeAsOneOrNull()
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

    override suspend fun searchCards(
        query: String,
        limit: Long,
    ): List<String> =
        withContext(Dispatchers.IO) {
            queries.fuzzySearchCard(query, limit).executeAsList()
        }

    override suspend fun insert(
        id: Uuid,
        name: String,
        colors: List<String>?,
        legalities: Map<String, Boolean>,
        type: String,
        imageSource: String,
        cropImageSource: String,
    ) {
        withContext(Dispatchers.IO) {
            queries.insertCard(
                id,
                name,
                colors,
                legalities,
                type,
                imageSource,
                cropImageSource,
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
                    type,
                    imageSource,
                    cropImageSource,
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
                        card.type,
                        card.imageSource,
                        card.cropImageSource,
                    )
                }
            }
        }
    }

    override suspend fun insertMetadata(
        key: String,
        value: String,
    ) {
        withContext(Dispatchers.IO) {
            queries.insertCardMetadata(key, value)
        }
    }

    override suspend fun getMetadata(key: String): String? =
        withContext(Dispatchers.IO) {
            queries.getCardMetadata(key).executeAsOneOrNull()
        }
}
