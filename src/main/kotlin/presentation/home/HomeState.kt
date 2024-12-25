package presentation.home

import kotlinx.datetime.LocalDateTime

data class HomeState(
    val isLoadingCards: Boolean = false,
    val bulkFetchDate: LocalDateTime? = null,
)
