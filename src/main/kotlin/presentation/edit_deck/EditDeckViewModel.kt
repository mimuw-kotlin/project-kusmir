package presentation.edit_deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import domain.model.Deck
import domain.model.DeckList
import domain.use_cases.cards.CardsUseCases
import domain.use_cases.deck.DecksUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditDeckViewModel(
    private val deckUseCases: DecksUseCases,
    private val cardsUseCases: CardsUseCases,
    val deckId: Long
): ViewModel() {

    private val _state = MutableStateFlow(EditDeckState(deckId = deckId))
    val state: StateFlow<EditDeckState> = _state

    init {
        viewModelScope.launch {
            _state.value = state.value.copy(
                deckName = "New deck",
                imageUrl = "https://cards.scryfall.io/normal/front/5/6/56ebc372-aabd-4174-a943-c7bf59e5028d.jpg?1562629953",
                mainDeck = DeckList(),
                sideboard= DeckList()
            )

            deckUseCases.getDeck(deckId)?.also { deck ->
                _state.value = state.value.copy(
                    deckName = deck.name,
                    imageUrl = "https://cards.scryfall.io/normal/front/5/6/56ebc372-aabd-4174-a943-c7bf59e5028d.jpg?1562629953",
                    mainDeck = deck.mainDeck,
                    sideboard = deck.sideboard,
                )
            }
        }
    }

    data class Section(
        val name: String,
        val cardNamesWithCount: List<Pair<String, Long>>
    )

    fun getFormattedDeckList(): List<Section> {
        return listOf(Section("asdf", emptyList()))
    }

    fun onEvent(event: EditDeckEvent) {
        when (event) {
            is EditDeckEvent.AddCard -> {
                viewModelScope.launch {
                    val card = cardsUseCases.getCardByName(event.cardName)

                    assert(event.type == null)
                    val target = event.type?: if (state.value.addCardMenuState.selectedDeckTypeId == 0) {
                        Deck.ListType.MainDeck
                    } else {
                        Deck.ListType.Sideboard
                    }

                    card?.let {
                        println(it.name)
                        when (target) {
                            Deck.ListType.MainDeck ->
                                _state.value = state.value.copy(
                                    mainDeck = _state.value.mainDeck
                                        .toMutableDeckList()
                                        .apply { addCard(card) }
                                        .toDeckList()
                                )
                            Deck.ListType.Sideboard ->
                                _state.value = state.value.copy(
                                    sideboard = _state.value.sideboard
                                        .toMutableDeckList()
                                        .apply { addCard(card) }
                                        .toDeckList()
                                )
                        }

                        _state.value = state.value.copy(
                            addCardMenuState = state.value.addCardMenuState.copy(
                                searchBoxState = state.value.addCardMenuState.searchBoxState.copy(
                                    query = ""
                                )
                            )
                        )
                    }
                }
            }

            is EditDeckEvent.EnteredDeckName -> {
                _state.value = state.value.copy(deckName = event.name)
                println("name: ${state.value.deckName}")
            }

            is EditDeckEvent.CardSearch -> {
                _state.value = state.value.copy(
                    addCardMenuState = state.value.addCardMenuState.copy(
                        searchBoxState = state.value.addCardMenuState.searchBoxState.copy(
                            query = event.query
                        )
                    )
                )
                viewModelScope.launch {
                    _state.value = state.value.copy(
                        addCardMenuState = state.value.addCardMenuState.copy(
                            searchBoxState = state.value.addCardMenuState.searchBoxState.copy(
                                results = cardsUseCases.getSearchResults(event.query)
                            )
                        )
                    )
                }
            }

            is EditDeckEvent.RemoveCard -> {
                viewModelScope.launch {
                    val card = cardsUseCases.getCardByName(event.cardName)
                    card?.let {
                        when (event.type) {
                            Deck.ListType.MainDeck ->
                                _state.value = state.value.copy(
                                    mainDeck = _state.value.mainDeck
                                        .toMutableDeckList()
                                        .apply { removeCard(card) }
                                        .toDeckList()
                                )
                            Deck.ListType.Sideboard ->
                                _state.value = state.value.copy(
                                    sideboard = _state.value.sideboard
                                        .toMutableDeckList()
                                        .apply { removeCard(card) }
                                        .toDeckList()
                                )
                        }
                    }
                }
            }

            is EditDeckEvent.SaveDeck -> {
                val deck = Deck(
                    id = state.value.deckId,
                    name = state.value.deckName,
                    mainDeck = state.value.mainDeck,
                    sideboard = state.value.sideboard,
                )

                viewModelScope.launch {
                    val id = deckUseCases.saveDeck(deck)
                    _state.value = state.value.copy(
                        deckId = id
                    )
                }
            }

            is EditDeckEvent.ToggleAddCardMenu -> {
                _state.value = state.value.copy(
                    addCardMenuState = state.value.addCardMenuState.copy(
                        isExpanded = !state.value.addCardMenuState.isExpanded
                    ),
                )
            }

            is EditDeckEvent.SelectTargetDeckListType -> {
                _state.value = state.value.copy(
                    addCardMenuState = state.value.addCardMenuState.copy(
                        selectedDeckTypeId = event.id
                    ),
                )
            }

            is EditDeckEvent.ToggleImportPopup -> {
                _state.value = state.value.copy(
                    importDeckState = state.value.importDeckState.copy(
                        isVisible = !state.value.importDeckState.isVisible,
                        input = "",
                    )
                )
            }

            is EditDeckEvent.EnteredDeckImportValue -> {
                _state.value = state.value.copy(
                    importDeckState = state.value.importDeckState.copy(
                        input = event.input
                    )
                )
            }

            is EditDeckEvent.ImportDeck -> {
                val deckCode = state.value.importDeckState.input
                _state.value = state.value.copy(
                    importDeckState = state.value.importDeckState.copy(
                        isVisible = false,
                        input = "",
                    )
                )

                viewModelScope.launch {
                    try {
                        val deck = deckUseCases.importDeck(deckCode)
                        _state.value = state.value.copy(
                            mainDeck = deck.mainDeck,
                            sideboard = deck.sideboard,
                        )
                    } catch (e: IllegalArgumentException) {
                        println(e.message) // TODO: error popup
                    }
                }
            }

            is EditDeckEvent.DeleteDeck -> {
                viewModelScope.launch {
                    deckUseCases.deleteDeckUseCase(state.value.deckId)
                }
            }
        }
    }
}