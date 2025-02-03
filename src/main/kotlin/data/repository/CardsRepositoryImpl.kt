package data.repository

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import data.local.database.CardDb
import data.network.ScryfallApi
import data.repository.util.toDatabase
import data.repository.util.toDomain
import data.source.CardsDao
import domain.model.Card
import domain.repository.CardsRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import kotlin.uuid.Uuid

const val BULK_INSERT_SIZE = 5000

class CardsRepositoryImpl(
    private val cardsDao: CardsDao,
    private val scryfallApi: ScryfallApi,
) : CardsRepository {
    override suspend fun getCardById(id: Uuid): Card? {
        val card = cardsDao.getById(id)
        return card?.toDomain()
    }

    override suspend fun getCardByMtgoId(mtgoId: Long): Card? {
        val card = cardsDao.getByMtgoId(mtgoId)
        if (card != null) {
            return card.toDomain()
        } else {
            val response = scryfallApi.fetchCardByMtgoId(mtgoId)
            if (!response.status.isSuccess()) return null

            val stringBody: String = response.body()
            val cardDb = Gson().fromJson(stringBody, JsonObject::class.java).toDatabase()
            return cardDb
                ?.also {
                    cardsDao.insert(cardDb)
                }?.toDomain()
        }
    }

    override suspend fun getCardByName(name: String): Card? {
        val card = cardsDao.getByName(name)
        return card?.toDomain()
    }

    override suspend fun fetchAndUpdateCardsData() {
        val tempFile =
            withContext(Dispatchers.IO) {
                File.createTempFile("cards", ".json")
            }

        scryfallApi.getCardsChannel("oracle_cards").copyAndClose(tempFile.writeChannel())

        val gson = Gson()
        val inputStream =
            withContext(Dispatchers.IO) {
                FileInputStream(tempFile)
            }

        inputStream.use { input ->
            JsonReader(InputStreamReader(input)).use { jsonReader ->
                if (jsonReader.peek() != JsonToken.BEGIN_ARRAY) {
                    throw IOException("Expected an array at the root of JSON data")
                }

                jsonReader.beginArray()

                val allCards: MutableList<CardDb> = mutableListOf()
                while (jsonReader.hasNext() && jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                    val jsonObject = gson.fromJson<JsonElement>(jsonReader, JsonElement::class.java).asJsonObject
                    jsonObject.toDatabase()?.let { allCards.add(it) }

                    if (allCards.size >= BULK_INSERT_SIZE) {
                        cardsDao.insertMultiple(allCards)
                        allCards.clear()
                    }
                }

                if (allCards.isNotEmpty()) {
                    cardsDao.insertMultiple(allCards)
                }
            }
        }

        val currentTime = Clock.System.now()
        cardsDao.insertMetadata("lastFetchInstant", currentTime.toString())

        tempFile.delete()
    }

    override suspend fun getCardsSearchResults(
        query: String,
        limit: Long,
    ): List<String> = cardsDao.searchCards(query, limit)

    override suspend fun getLastFetchInstant(): Instant? {
        val instantString: String? = cardsDao.getMetadata("lastFetchInstant")
        return instantString?.let { Instant.parse(it) }
    }
}
