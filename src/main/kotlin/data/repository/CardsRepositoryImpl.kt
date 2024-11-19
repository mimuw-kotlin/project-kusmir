package data.repository

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import data.data_source.CardsDao
import data.local.database.CardDb
import data.network.ScryfallApi
import data.repository.util.CardModelAdapter
import domain.model.Card
import domain.repository.CardsRepository
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import kotlin.uuid.Uuid

class CardsRepositoryImpl(
    private val cardsDao: CardsDao,
    private val scryfallApi: ScryfallApi
): CardsRepository {
    private val cardModelAdapter = CardModelAdapter()

    override suspend fun getCardById(id: Uuid): Card? {
        val card = cardsDao.getById(id)
        return card?.let{ cardModelAdapter.toDomain(card) }
    }

    override suspend fun getCardByName(name: String): Card? {
        val card = cardsDao.getByName(name)
        return card?.let{ cardModelAdapter.toDomain(card) }
    }

    override suspend fun fetchAndUpdateCardsData() {
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile("cards", ".json")
        }

        scryfallApi.getCardsChannel("oracle_cards").copyAndClose(tempFile.writeChannel())

        val gson = Gson()
        val inputStream = withContext(Dispatchers.IO) {
            FileInputStream(tempFile)
        }

        inputStream.use { input ->
            JsonReader(InputStreamReader(input)).use { jsonReader ->
                if (jsonReader.peek() != JsonToken.BEGIN_ARRAY)
                    throw IOException("Expected an array at the root of JSON data")

                jsonReader.beginArray()

                val allCards: MutableList<CardDb> = mutableListOf()
                while (jsonReader.hasNext() && jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                    val jsonObject = gson.fromJson<JsonElement>(jsonReader, JsonElement::class.java).asJsonObject
                    allCards.add(cardModelAdapter.fromJsonToDatabase(jsonObject))

                    if (allCards.size >= 5000) {
                        cardsDao.insertMultiple(allCards)
                        allCards.clear()
                    }
                }

                if (allCards.isNotEmpty())
                    cardsDao.insertMultiple(allCards)
            }
        }

        tempFile.delete()
    }
}