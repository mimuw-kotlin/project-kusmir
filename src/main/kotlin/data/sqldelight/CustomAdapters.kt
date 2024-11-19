package data.sqldelight

import app.cash.sqldelight.ColumnAdapter

interface CustomAdapters {
    fun ListStringAdapter(): ColumnAdapter<List<String>, String>
    fun legalitiesAdapter(): ColumnAdapter<Map<String, Boolean>, String>
}