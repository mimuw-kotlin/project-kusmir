package data.source

import data.local.database.GameReportDb
import data.local.database.MatchReportDb
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface MatchReportDao {
    // Creates a new MatchReport entry and returns its id.
    suspend fun createMatchReport(
        opponentName: String,
        date: Long,
        structure: Long,
        format: String?,
        registeredDeckId: Long,
    ): Long

    // Creates a new GameReport entry and returns its id.
    suspend fun createGameReport(
        matchReportId: Long,
        result: Long,
        isOnTePlay: Boolean,
        playerMulligan: Long,
        opponentMulligan: Long,
        opponentRevealedCardsIds: List<Uuid>,
        playerDrawnCardsIds: List<Uuid>,
        cardsSidedOutIds: List<Uuid>,
        cardsSidedInIds: List<Uuid>,
    ): Long

    suspend fun updateMatchReport(matchReportDb: MatchReportDb)

    fun getAllMatchReports(): Flow<List<MatchReportDb>>

    fun getMatchReportsByDeck(deckId: Long): Flow<List<MatchReportDb>>

    suspend fun getMatchReportById(id: Long): MatchReportDb?

    fun getGameReportsByMatchReportId(matchReportId: Long): List<GameReportDb>
}
