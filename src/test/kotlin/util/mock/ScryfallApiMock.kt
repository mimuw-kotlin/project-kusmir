@file:Suppress("ktlint:standard:no-wildcard-imports")

package util.mock

import data.local.database.CardDb
import data.network.ScryfallApi
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
    override suspend fun fetchCardById(id: String): CardDb? {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCardByMtgoId(mtgoId: Long): CardDb? {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCardByName(name: String): CardDb? {
        TODO("Not yet implemented")
    }

    override suspend fun fetchBulkData(type: String): ScryfallApi.BulkData {
        val filePath =
            javaClass.classLoader
                .getResource(bulkFileName)
                ?.path
                ?: throw IllegalArgumentException("$bulkFileName not found")

        val file = File(filePath)
        return ScryfallApi.BulkData(
            size = file.length().toInt(),
            content = file.readChannel(),
        )
    }

    private fun File.readChannel(): ByteReadChannel =
        runBlocking {
            ByteReadChannel(this@readChannel.readBytes())
        }
}
