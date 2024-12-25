package data.source

import data.local.database.CardDb
import kotlin.uuid.Uuid

interface CardsDao {
    suspend fun getById(id: Uuid): CardDb?

    suspend fun getByName(name: String): CardDb?

    suspend fun deleteWithId(id: Uuid)

    suspend fun deleteWithIds(ids: List<Uuid>)

    suspend fun searchCards(
        query: String,
        limit: Long = 10,
    ): List<String>

    suspend fun insert(
        id: Uuid,
        name: String,
        colors: List<String>?,
        legalities: Map<String, Boolean>,
        type: String,
        imageSource: String,
        cropImageSource: String,
    )

    suspend fun insert(card: CardDb)

    suspend fun insertMultiple(cards: List<CardDb>)

    suspend fun insertMetadata(
        key: String,
        value: String,
    )

    suspend fun getMetadata(key: String): String?
}
