@file:OptIn(ExperimentalUuidApi::class)

package data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.softwork.uuid.sqldelight.UuidByteArrayAdapter
import app.softwork.uuid.toUuid
import data.local.database.CardDb
import data.local.database.Card_deck
import data.local.database.Database
import data.source.CardsDaoImpl
import data.sqldelight.CustomAdaptersImpl
import domain.model.MtgColor
import domain.repository.CardsRepository
import kotlinx.coroutines.test.runTest
import mock.ScryfallApiMock
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.uuid.ExperimentalUuidApi

@Ignore
class CardsRepositoryFullDataTest {
    companion object {
        lateinit var cardsRepository: CardsRepository

        @OptIn(ExperimentalUuidApi::class)
        @BeforeAll
        @JvmStatic
        fun setup() =
            runTest(timeout = 5.toDuration(DurationUnit.MINUTES)) {
//            val driver = DatabaseDriverFactory().createDriver()
                val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
                Database.Schema.create(driver)
                val db =
                    Database(
                        driver = driver,
                        cardDbAdapter =
                            CardDb.Adapter(
                                idAdapter = UuidByteArrayAdapter,
                                colorsAdapter = CustomAdaptersImpl().listStringAdapter(),
                                legalitiesAdapter = CustomAdaptersImpl().legalitiesAdapter(),
                            ),
                        card_deckAdapter =
                            Card_deck.Adapter(
                                cardIdAdapter = UuidByteArrayAdapter,
                            ),
                    )

                val dao = CardsDaoImpl(db)
                val scryfallApi = ScryfallApiMock("OracleCardsFull.json")
                cardsRepository =
                    CardsRepositoryImpl(
                        cardsDao = dao,
                        scryfallApi = scryfallApi,
                    )

                cardsRepository.fetchAndUpdateCardsData()
            }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun testGetById() =
        runTest {
            val id = "55e4b27e-5447-4fc2-8cae-a03e344600c6".toUuid()
            val actualName = "Nicol Bolas, the Ravager // Nicol Bolas, the Arisen"

            val card = cardsRepository.getCardById(id)

            assertNotNull(card)
            assertEquals(actualName, card.name)
            assertEquals(id, card.id)
            assertEquals(
                "Legendary Creature — Elder Dragon // Legendary Planeswalker — Bolas",
                card.type,
            )
            assertEquals(
                setOf(MtgColor.BLUE, MtgColor.RED, MtgColor.BLACK),
                card.colorIdentity,
            )
        }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun testGetByNameSimple() =
        runTest {
            val name = "Lightning Bolt"
            val expectedId = "4457ed35-7c10-48c8-9776-456485fdf070".toUuid()

            val card = cardsRepository.getCardByName(name)

            assertNotNull(card)
            assertEquals(name, card.name)
            assertEquals(expectedId, card.id)
            assertEquals("Instant", card.type)
            assertEquals(setOf(MtgColor.RED), card.colorIdentity)
        }

    @Test
    fun testGetSearchResults() =
        runTest {
            val query = "Lightning"

            val results = cardsRepository.getCardsSearchResults(query)

            println(results)
            assert(results.isNotEmpty())
        }
}
