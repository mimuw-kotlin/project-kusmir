package domain.usecases.tracking

data class TrackingUseCases(
    val getLogFileUseCase: GetLogFileUseCase,
    val readLogUseCase: ReadLogUseCase,
)
