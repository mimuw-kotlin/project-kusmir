package presentation.home

import kotlinx.datetime.LocalDateTime

data class HomeState(
    val isLoadingCards: Boolean = false,
    val downloadProgress: Float = 0f,
    val bulkFetchDate: LocalDateTime? = null,
)
