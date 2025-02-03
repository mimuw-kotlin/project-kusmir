package data.network

import app.softwork.uuid.toUuid
import com.google.gson.Gson
import com.google.gson.JsonObject
import data.local.database.CardDb
import data.repository.util.parseLegalityString
import data.repository.util.toDatabase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

const val DELAY_MS: Long = 100
const val BASE_URL = "https://api.scryfall.com"

class ScryfallApiImpl : ScryfallApi {
    private val httpClient =
        HttpClient(Apache) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        useAlternativeNames = false
                    },
                )
            }
            defaultRequest {
                headers {
                    append("User-Agent", "mtgo-tracker-dev")
                    append("Accept", "application/json;q=0.9,*/*;q=0.8")
                }
            }
        }

    private val mutex = Mutex()
    private var lastCallTime: Long = System.currentTimeMillis()

    private suspend fun performApiCallWithDelay(url: String): HttpResponse {
        mutex.withLock {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastCallTime

            if (elapsedTime < DELAY_MS) {
                delay(DELAY_MS - elapsedTime)
            }

            lastCallTime = System.currentTimeMillis()
        }

        val response = httpClient.get(url)
        if (!response.status.isSuccess()) {
            throw ResponseException(response, "HTTP error: ${response.status}")
        }
        return response
    }

    override suspend fun fetchCardById(id: String): CardDb? {
        val requestUrl = "$BASE_URL/cards/$id"
        val response = performApiCallWithDelay(requestUrl)

        return Gson().fromJson(response.bodyAsText(), JsonObject::class.java).toDatabase()
    }

    override suspend fun fetchCardByMtgoId(mtgoId: Long): CardDb? {
        val requestUrl = "$BASE_URL/cards/mtgo/$mtgoId"
        val response = performApiCallWithDelay(requestUrl)

        return Gson().fromJson(response.bodyAsText(), JsonObject::class.java).toDatabase()
    }

    override suspend fun fetchCardByName(name: String): CardDb? {
        val requestUrl = "$BASE_URL/cards/named?exact=$name".replace(" ", "%20")
        val response = performApiCallWithDelay(requestUrl)

        return Gson().fromJson(response.bodyAsText(), JsonObject::class.java).toDatabase()
    }

    override suspend fun fetchBulkData(type: String): ScryfallApi.BulkData {
        val requestUrl = "$BASE_URL/bulk-data/$type"
        val response = performApiCallWithDelay(requestUrl)

        val json = Gson().fromJson(response.bodyAsText(), JsonObject::class.java)

        val fileSize = json.get("size").asInt
        val downloadUri = json.get("download_uri").asString

        return ScryfallApi.BulkData(
            size = fileSize,
            content =
                httpClient
                    .get {
                        url(downloadUri)
                        method = HttpMethod.Get
                    }.bodyAsChannel()
        )
    }
}
