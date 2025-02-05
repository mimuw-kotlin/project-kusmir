package data.network

import data.local.database.CardDb
import io.ktor.client.statement.*
import io.ktor.utils.io.*

interface ScryfallApi {
    suspend fun fetchCardById(id: String): CardDb?

    suspend fun fetchCardByMtgoId(mtgoId: Long): CardDb?

    suspend fun fetchCardByName(name: String): CardDb?

    data class BulkData(
        val size: Int,
        val content: ByteReadChannel,
    )

    suspend fun fetchBulkData(type: String): BulkData
}
