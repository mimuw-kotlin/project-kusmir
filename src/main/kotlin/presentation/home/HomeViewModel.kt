package presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecases.cards.CardsUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val cardsUseCases: CardsUseCases,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    bulkFetchDate = cardsUseCases.getLastFetchDateTime(),
                )
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.FetchCardsData -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoadingCards = true)
                    cardsUseCases.fetchCardsData()

                    val currentTime =
                        Clock.System
                            .now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())

                    _state.value =
                        _state.value.copy(
                            isLoadingCards = false,
                            bulkFetchDate = currentTime,
                        )
                }
            }
        }
    }
}
