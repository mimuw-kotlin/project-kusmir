package data.data_source

import data.local.database.CardDb
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CardsDao {
    suspend fun getAll(): Flow<List<CardDb>>
    suspend fun getById(id: Uuid): CardDb?
    suspend fun getByName(name: String): CardDb?
    suspend fun deleteWithId(id: Uuid)
    suspend fun deleteWithIds(ids: List<Uuid>)
    suspend fun insert(
        id: Uuid,
        name: String,
        colors: List<String>?,
        legalities: Map<String, Boolean>,
        imageSource: String
    )
    suspend fun insert(card: CardDb)
    suspend fun insertMultiple(cards: List<CardDb>)
}