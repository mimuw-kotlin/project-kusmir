package domain.usecases.cards

import domain.repository.CardsRepository
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetLastFetchDateTimeUseCase(
    private val cardsRepository: CardsRepository,
) {
    suspend operator fun invoke(): LocalDateTime? {
        val lastFetchInstant = cardsRepository.getLastFetchInstant()
        val timeZone = TimeZone.currentSystemDefault()
        return lastFetchInstant?.toLocalDateTime(timeZone)
    }
}
