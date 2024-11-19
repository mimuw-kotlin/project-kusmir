package data.network

import io.ktor.client.statement.*
import io.ktor.utils.io.*

interface ScryfallApi {
    suspend fun fetchCardById(id: String): HttpResponse
    suspend fun fetchCardByName(name: String): HttpResponse
    suspend fun fetchBulkData(type: String): HttpResponse
    suspend fun getCardsChannel(type: String): ByteReadChannel
}