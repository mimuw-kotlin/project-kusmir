import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.local.database.Database
import data.local.database.GameReportDb
import data.local.database.MatchReportDb
import data.source.MatchReportDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class MatchReportDaoImpl(
    private val db: Database,
): MatchReportDao {
    private val queries = db.databaseQueries

    override suspend fun createMatchReport(
        opponentName: String,
        date: Long,
        structure: Long,
        format: String?,
        registeredDeckId: Long
    ): Long =
        withContext(Dispatchers.IO) {
            queries.transactionWithResult {
                queries.insertMatchReport(
                    id = null,
                    opponentName = opponentName,
                    date = date,
                    structure = structure,
                    format = format,
                    registeredDeckId = registeredDeckId
                )
                queries.getLastInsertedId().executeAsOne()
            }
        }

    override suspend fun createGameReport(
        matchReportId: Long,
        result: Long,
        isOnTePlay: Boolean,
        playerMulligan: Long,
        opponentMulligan: Long,
        opponentRevealedCardsIds: List<Uuid>,
        playerDrawnCardsIds: List<Uuid>,
        cardsSidedOutIds: List<Uuid>,
        cardsSidedInIds: List<Uuid>,
    ): Long =
        withContext(Dispatchers.IO) {
            queries.transactionWithResult {
                queries.insertGameReport(
                    id = null,
                    matchReportId = matchReportId,
                    result = result,
                    isOnThePlay = isOnTePlay,
                    playerMulligan = playerMulligan,
                    opponentMulligan = opponentMulligan,
                    opponentRevealedCardsIds = opponentRevealedCardsIds,
                    playerDrawnCardsIds = playerDrawnCardsIds,
                    cardsSidedOutIds = cardsSidedOutIds,
                    cardsSidedInIds = cardsSidedInIds,
                )
                queries.getLastInsertedId().executeAsOne()
            }
        }

    override suspend fun updateMatchReport(matchReportDb: MatchReportDb) =
        withContext(Dispatchers.IO) {
            with(matchReportDb) {
                queries.insertMatchReport(
                    id = this.id,
                    opponentName = this.opponentName,
                    date = this.date,
                    structure = this.structure,
                    format = this.format,
                    registeredDeckId = this.registeredDeckId,
                )
            }
        }

    override fun getAllMatchReports(): Flow<List<MatchReportDb>> {
        return queries.selectAllMatchReports()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun getMatchReportsByDeck(deckId: Long): Flow<List<MatchReportDb>> {
        return queries.selectMatchReportsByDeckId(deckId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    override suspend fun getMatchReportById(id: Long): MatchReportDb? =
        withContext(Dispatchers.IO) {
            queries.selectMatchReportById(id).executeAsOneOrNull()
        }

    override fun getGameReportsByMatchReportId(matchReportId: Long): List<GameReportDb> =
        queries.selectGameReportsByMatchId(matchReportId).executeAsList()
}
