package data.repository

import data.local.database.GameReportDb
import data.local.database.MatchReportDb
import data.repository.util.toDatabase
import data.repository.util.toDomain
import data.repository.util.toMtgFormat
import data.source.CardsDao
import data.source.MatchReportDao
import domain.model.Card
import domain.model.GameReport
import domain.model.GameResult
import domain.model.MatchReport
import domain.model.MtgFormat
import domain.repository.MatchReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.uuid.Uuid

class MatchReportRepositoryImpl(
    private val matchReportDao: MatchReportDao,
    private val cardsDao: CardsDao,
): MatchReportRepository {
    private suspend fun List<Uuid>.toCardsList(): List<Card> =
        this.map { cardsDao.getById(it)?.toDomain() ?: error("Card with id $it does not exist.")}

    private suspend fun GameReportDb.toDomain(): GameReport =
        GameReport(
            result = when (this.result) {
                -1L -> GameResult.LOST
                0L  -> GameResult.DRAW
                1L  -> GameResult.WON
                else -> error("Invalid result value ${this.result}")
            },
            isOnThePlay = this.isOnThePlay,
            opponentRevealedCards = this.opponentRevealedCardsIds.toCardsList(),
            playerDrawnCards = this.playerDrawnCardsIds.toCardsList(),
            cardsSidedOut = this.cardsSidedOutIds.toCardsList(),
            cardsSidedIn = this.cardsSidedInIds.toCardsList(),
            playerMulligan = this.playerMulligan.toInt(),
            opponentMulligan = this.opponentMulligan.toInt(),
        )

    private suspend fun MatchReportDb.toDomain(): MatchReport {
        return MatchReport(
            id = this.id,
            opponentName = this.opponentName,
            date = Date(this.date),
            structure = when (this.structure) {
                1L -> MatchReport.Structure.Bo1
                3L -> MatchReport.Structure.Bo3
                else -> error(IllegalStateException("DB structure value ${this.structure} is invalid"))
            },
            format = this.format?.toMtgFormat() ?: MtgFormat.UNKNOWN,
            registeredDeckId = this.registeredDeckId,
            gameReports =
                matchReportDao.getGameReportsByMatchReportId(this.id)
                    .map {
                        it.toDomain()
                    }
        )
    }

    override suspend fun createMatchReport(
        opponentName: String,
        date: Date,
        structure: MatchReport.Structure,
        format: MtgFormat,
        registeredDeckId: Long,
        gameReports: List<GameReport>
    ): MatchReport {
        val reportId = matchReportDao.createMatchReport(
            opponentName = opponentName,
            structure = structure.toDatabase(),
            date = date.toInstant().toEpochMilli(),
            format = format.toString(),
            registeredDeckId = registeredDeckId,
        )

        gameReports.forEach {
            matchReportDao.createGameReport(
                matchReportId = reportId,
                result = it.result.toDatabase(),
                isOnTePlay = it.isOnThePlay,
                playerMulligan = it.playerMulligan.toLong(),
                opponentMulligan = it.opponentMulligan.toLong(),
                opponentRevealedCardsIds = it.opponentRevealedCards.toDatabase(),
                playerDrawnCardsIds = it.playerDrawnCards.toDatabase(),
                cardsSidedOutIds = it.cardsSidedOut.toDatabase(),
                cardsSidedInIds = it.cardsSidedIn.toDatabase(),
            )
        }

        return MatchReport(
            id = reportId,
            opponentName = opponentName,
            date = date,
            structure = structure,
            format = format,
            registeredDeckId = registeredDeckId,
            gameReports = gameReports,
        )
    }

    override suspend fun updateMatchReport(matchReport: MatchReport) =
        with(matchReport) {
            matchReportDao.updateMatchReport(
                MatchReportDb(
                    id = this.id,
                    opponentName = this.opponentName,
                    date = this.date.toInstant().toEpochMilli(),
                    structure = this.structure.toDatabase(),
                    format = this.format.toDatabase(),
                    registeredDeckId = this.registeredDeckId,
                )
            )
        }

    override suspend fun getById(id: Long): MatchReport? =
        matchReportDao.getMatchReportById(id)?.toDomain()

    override fun getAll(): Flow<List<MatchReport>> =
        matchReportDao.getAllMatchReports().map { matchReports ->
            matchReports.map { it.toDomain() }
        }

    override fun getALlByDeckId(deckId: Long): Flow<List<MatchReport>> =
        matchReportDao.getMatchReportsByDeck(deckId).map { matchReports ->
            matchReports.map { it.toDomain() }
        }
}