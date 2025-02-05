package domain.repository

import domain.model.GameReport
import domain.model.MatchReport
import domain.model.MtgFormat
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MatchReportRepository {
    suspend fun createMatchReport(
        opponentName: String,
        date: Date,
        structure: MatchReport.Structure,
        format: MtgFormat,
        registeredDeckId: Long,
        gameReports: List<GameReport>,
    ): MatchReport

    suspend fun updateMatchReport(matchReport: MatchReport)

    suspend fun getById(id: Long): MatchReport?

    fun getAll(): Flow<List<MatchReport>>

    fun getALlByDeckId(deckId: Long): Flow<List<MatchReport>>
}
