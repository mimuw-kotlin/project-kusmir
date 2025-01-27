package domain.usecases.tracking

data class TrackingUseCases(
    val parseMatchLog: ParseMatchLogUseCase,
    val readLog: ReadLogUseCase,
)
