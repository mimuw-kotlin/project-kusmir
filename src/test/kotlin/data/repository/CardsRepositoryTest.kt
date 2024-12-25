@file:OptIn(ExperimentalUuidApi::class)

package data.repository

import app.softwork.uuid.toUuid
import data.source.CardsDaoImpl
import domain.model.MtgColor
import domain.repository.CardsRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import util.mock.ScryfallApiMock
import util.mockCardDatabase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi

class CardsRepositoryTest {
    lateinit var cardsRepository: CardsRepository

    @BeforeEach
    fun setUp() {
        cardsRepository =
            CardsRepositoryImpl(
                cardsDao = CardsDaoImpl(mockCardDatabase()),
                scryfallApi = ScryfallApiMock("OracleCardsMock.json"),
            )

        runBlocking {
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
            assertEquals(setOf(MtgColor.RED), card.colorIdentity)
        }

    @Test
    fun testGetByNameDualFacedCard() =
        runTest {
        }
}
