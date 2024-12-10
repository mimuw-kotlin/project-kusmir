package presentation.home

sealed class HomeEvent {
    data object FetchCardsData : HomeEvent()
}
