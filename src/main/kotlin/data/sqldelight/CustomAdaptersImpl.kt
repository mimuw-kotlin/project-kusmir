package data.sqldelight

import app.cash.sqldelight.ColumnAdapter

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
}
