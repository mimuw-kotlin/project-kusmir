@file:OptIn(ExperimentalUuidApi::class)

package util

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.softwork.uuid.sqldelight.UuidByteArrayAdapter
import data.local.database.CardDb
import data.local.database.Card_deck
import data.local.database.Database
import data.sqldelight.CustomAdaptersImpl
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal fun mockCardDatabase(): Database {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    Database.Schema.create(driver)

    return Database(
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
}

@OptIn(ExperimentalUuidApi::class)
internal fun sampleCardDbList(): List<CardDb> =
    listOf(
        CardDb(
            id = Uuid.random(),
            name = "Test Card One",
            colors = listOf("W"),
            legalities =
                mapOf(
                    "standard" to false,
                    "pioneer" to true,
                ),
            type = "Creature",
            imageSource = "invalid/image/source",
            cropImageSource = "invalid/image/source",
        ),
        CardDb(
            id = Uuid.random(),
            name = "Test Card Two",
            colors = listOf("R", "G", "B"),
            legalities =
                mapOf(
                    "modern" to false,
                    "standard" to true,
                    "pioneer" to true,
                ),
            type = "Instant",
            imageSource = "invalid/image/source",
            cropImageSource = "invalid/image/source",
        ),
    )
