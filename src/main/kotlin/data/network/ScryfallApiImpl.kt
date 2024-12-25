package data.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.HttpClient
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

        return httpClient.get(url)
    }

    override suspend fun fetchCardById(id: String): HttpResponse {
        val requestUrl = "$BASE_URL/cards/$id"
        return performApiCallWithDelay(requestUrl)
    }

    override suspend fun fetchCardByName(name: String): HttpResponse {
        val requestUrl = "$BASE_URL/cards/named?exact=$name".replace(" ", "%20")
        return performApiCallWithDelay(requestUrl)
    }

    override suspend fun fetchBulkData(type: String): HttpResponse {
        val requestUrl = "$BASE_URL/bulk-data/$type"
        return performApiCallWithDelay(requestUrl)
    }

    override suspend fun getCardsChannel(type: String): ByteReadChannel {
        val url =
            Gson().fromJson(fetchBulkData(type).bodyAsText(), JsonObject::class.java)
                .get("download_uri").asString

        return httpClient.get {
            url(url)
            method = HttpMethod.Get
        }.bodyAsChannel()
    }
}
