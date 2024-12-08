package util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.local.database.Database
import java.io.File

const val DB_NAME = "test.db"

class DatabaseDriverFactory {
    fun createDriver(): SqlDriver {
        val isDebug = true
        val parentFolder =
            if (isDebug) {
                File(System.getProperty("java.io.tmpdir"))
            } else {
                File(System.getProperty("user.home") + "/mtg-tracker")
            }

        if (!parentFolder.exists()) {
            parentFolder.mkdirs()
        }

        val databasePath =
            if (isDebug) {
                File(System.getProperty("java.io.tmpdir"), DB_NAME)
            } else {
                File(parentFolder, DB_NAME)
            }

        return JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").also { driver ->
            Database.Schema.create(driver = driver)
        }
    }
}
