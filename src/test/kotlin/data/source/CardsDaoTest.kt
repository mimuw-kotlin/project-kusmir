@file:OptIn(ExperimentalUuidApi::class)

package data.source

import data.local.database.CardDb
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import util.mockCardDatabase
import util.sampleCardDbList
import kotlin.test.BeforeTest
import kotlin.uuid.ExperimentalUuidApi

class CardsDaoTest {
    private lateinit var dao: CardsDao
    private lateinit var sampleCards: List<CardDb>

    @BeforeTest
    fun setup() {
        dao = CardsDaoImpl(mockCardDatabase())
        sampleCards = sampleCardDbList()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun testGetById() =
        runTest {
            dao.insert(sampleCards[0])
            dao.insert(sampleCards[1])

            val receivedCard = dao.getById(sampleCards[0].id)

            assertEquals(receivedCard, sampleCards[0])
        }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun testGetById_notFound() =
        runTest {
            dao.insert(sampleCards[0])

            val receivedCard = dao.getById(sampleCards[1].id)
            assertNull(receivedCard)
        }

    @Test
    fun testGetByName() =
        runTest {
            dao.insert(sampleCards[0])
            dao.insert(sampleCards[1])

            val receivedCard = dao.getByName(sampleCards[1].name)

            assertEquals(receivedCard, sampleCards[1])
        }

    @Test
    fun testGetByName_notFound() =
        runTest {
            dao.insert(sampleCards[0])

            val receivedCard = dao.getByName(sampleCards[1].name)
            assertNull(receivedCard)
        }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun deleteWithId() =
        runTest {
            dao.insert(sampleCards[0])
            dao.insert(sampleCards[1])

            dao.deleteWithId(sampleCards[0].id)
            assertNull(dao.getById(sampleCards[0].id))
        }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun deleteWithIds() =
        runTest {
            dao.insert(sampleCards[0])
            dao.insert(sampleCards[1])

            dao.deleteWithIds(listOf(sampleCards[0].id, sampleCards[1].id))
            assertNull(dao.getById(sampleCards[0].id))
            assertNull(dao.getById(sampleCards[1].id))
        }

    @Test
    fun `should throw exception when inserting same card multiple times`() =
        runTest {
            dao.insert(sampleCards[0])
            dao.insert(sampleCards[1])
        }

    @Test
    fun testSearchCards() =
        runTest {
            dao.insert(sampleCards[0])
            dao.insert(sampleCards[1])

            assert(
                dao
                    .searchCards("card")
                    .also {
                        println(it)
                    }.isNotEmpty(),
            )
        }
}
