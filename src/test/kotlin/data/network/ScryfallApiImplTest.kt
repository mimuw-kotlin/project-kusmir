package data.network

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.BeforeTest

class ScryfallApiImplTest {
    private lateinit var api: ScryfallApi

    @BeforeTest
    fun setup() {
        api = ScryfallApiImpl()
    }

    @Test
    fun fetchCardById() {
    }

    @Test
    fun fetchCardByName() = runTest{
    }

    @Test
    fun fetchBulkData() {
    }

    @Test
    fun getCardsChannel() {
    }
}