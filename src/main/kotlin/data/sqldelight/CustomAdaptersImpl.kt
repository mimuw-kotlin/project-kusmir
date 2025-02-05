package data.sqldelight

import app.cash.sqldelight.ColumnAdapter
import kotlin.uuid.Uuid

class CustomAdaptersImpl : CustomAdapters {
    override fun listStringAdapter(): ColumnAdapter<List<String>, String> =
        object : ColumnAdapter<List<String>, String> {
            override fun decode(databaseValue: String): List<String> =
                if (databaseValue.isBlank()) {
                    emptyList()
                } else {
                    databaseValue.split(',')
                }

            override fun encode(value: List<String>): String = value.joinToString(separator = ",")
        }

    override fun legalitiesAdapter(): ColumnAdapter<Map<String, Boolean>, String> =
        object : ColumnAdapter<Map<String, Boolean>, String> {
            override fun decode(databaseValue: String): Map<String, Boolean> =
                databaseValue
                    .split(",")
                    .map { it.split(":") }
                    .associate { it[0] to it[1].toBoolean() }

            override fun encode(value: Map<String, Boolean>): String = value.entries.joinToString(",") { "${it.key}:${it.value}" }
        }

    override fun listUuidAdapter(): ColumnAdapter<List<Uuid>, String> =
        object : ColumnAdapter<List<Uuid>, String> {
            override fun decode(databaseValue: String): List<Uuid> {
                return databaseValue
                    .split(",") // Split the string by commas
                    .filter { it.isNotBlank() } // Remove empty entries
                    .map { Uuid.parse(it) }
            }

            override fun encode(value: List<Uuid>): String {
                return value.joinToString(",") { it.toString() } // Join Uuids into a comma-separated string
            }
        }
}
