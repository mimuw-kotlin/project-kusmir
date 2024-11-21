package presentation.decks

import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import domain.use_cases.deck.DecksUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DecksViewModel(
    private val useCases: DecksUseCases
): ViewModel() {

    private val _state = mutableStateOf(DecksState())
    val state: State<DecksState> = _state

    private var getDecksJob: Job? = null

    init {
        getDecks()
    }

    private fun getDecks() {
        getDecksJob?.cancel()
        getDecksJob = useCases.getAllDecks()
            .onEach {decks ->
                _state.value = state.value.copy(
                    decks = decks
                )
            }
            .launchIn(viewModelScope)
    }

    //DEBUG ONLY
    fun fetchAndUpdateCards() {
        viewModelScope.launch {
            useCases.fetchAndUpdateCards()
            println("Fetched all data")
        }
    }
}