package data.sqldelight

import app.cash.sqldelight.ColumnAdapter
import kotlin.uuid.Uuid

interface CustomAdapters {
    fun listStringAdapter(): ColumnAdapter<List<String>, String>

    fun legalitiesAdapter(): ColumnAdapter<Map<String, Boolean>, String>

    fun listUuidAdapter(): ColumnAdapter<List<Uuid>, String>
}
