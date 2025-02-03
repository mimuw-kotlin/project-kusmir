package domain.usecases.statistics

data class StatisticsUseCases(
    val saveMatchReport: SaveMatchReportUseCase,
    val getAllMatchReports: GetAllMatchReportsUseCase,
)