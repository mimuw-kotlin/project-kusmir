package presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecases.deck.DecksUseCases
import domain.usecases.statistics.StatisticsUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StatisticsViewModel(
    private val decksUseCases: DecksUseCases,
    private val statisticsUseCases: StatisticsUseCases,
) : ViewModel() {
    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state

    init {
        decksUseCases
            .getAllDecks()
            .onEach { decks ->
                _state.value =
                    state.value.copy(
                        decks = decks.sortedBy { it.name },
                    )
            }.launchIn(viewModelScope)

        statisticsUseCases
            .getAllMatchReports()
            .onEach { reports ->
                _state.value =
                    state.value.copy(
                        matchReports = reports.sortedByDescending { it.date },
                    )
            }.launchIn(viewModelScope)
    }
}
