@file:Suppress("ktlint:standard:no-wildcard-imports")

package mock

import data.network.ScryfallApi
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import java.io.File

// {bulkFileName} should be either of
// 1. OracleCardsMock.json - contains 3 cards from SryfallApi:
//      - Nicol Bolas, the Ravager
//      - Lightning Bolt
//      - Plains
// 2. OracleCardsFull.json - full bulk data from ScryfallApi

class ScryfallApiMock(
    private val bulkFileName: String,
) : ScryfallApi {
    override suspend fun fetchCardById(id: String): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCardByName(name: String): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun fetchBulkData(type: String): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCardsChannel(type: String): ByteReadChannel {
        val filePath =
            javaClass.classLoader
                .getResource(bulkFileName)
                ?.path
                ?: throw IllegalArgumentException("$bulkFileName not found")

        val file = File(filePath)
        return file.readChannel()
    }

    private fun File.readChannel(): ByteReadChannel =
        runBlocking {
            ByteReadChannel(this@readChannel.readBytes())
        }
}
