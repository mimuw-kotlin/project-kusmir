package domain.usecases.statistics

import domain.model.MatchReport
import domain.repository.MatchReportRepository
import kotlinx.coroutines.flow.Flow

class GetAllMatchReportsUseCase(
    private val repository: MatchReportRepository
) {
    operator fun invoke(): Flow<List<MatchReport>> =
        repository.getAll()
}