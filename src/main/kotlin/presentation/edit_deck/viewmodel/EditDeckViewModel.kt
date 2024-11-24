package presentation.edit_deck.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.Deck
import domain.model.DeckList
import domain.use_cases.cards.CardsUseCases
import domain.use_cases.deck.DecksUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import presentation.edit_deck.EditDeckEvent
import presentation.edit_deck.EditDeckState

class EditDeckViewModel(
    private val deckUseCases: DecksUseCases,
    private val cardsUseCases: CardsUseCases,
    val deckId: Long
): ViewModel() {

    private val _editDeckState = MutableStateFlow(EditDeckState(deckId = deckId))
    val editDeckState: StateFlow<EditDeckState> = _editDeckState

    private val _addCardMenuState = MutableStateFlow(EditDeckState.AddCardMenuState())
    val addCardMenuState: StateFlow<EditDeckState.AddCardMenuState> = _addCardMenuState

    private val _chooseImageState = MutableStateFlow(EditDeckState.ChooseImageState())
    val chooseImageState: StateFlow<EditDeckState.ChooseImageState> = _chooseImageState

    private val _importDeckState = MutableStateFlow(EditDeckState.ImportDeckState())
    val importDeckState: StateFlow<EditDeckState.ImportDeckState> = _importDeckState

    private val _cardsImagesMap: SnapshotStateMap<String, String> = mutableStateMapOf()
    val cardsImagesMap: Map<String, String> = _cardsImagesMap

    init {
        viewModelScope.launch {
            _editDeckState.value = editDeckState.value.copy(
                deckName = "New deck",
                imageUrl = "https://cards.scryfall.io/normal/front/5/6/56ebc372-aabd-4174-a943-c7bf59e5028d.jpg?1562629953",
                mainDeck = DeckList(),
                sideboard= DeckList()
            )

            deckUseCases.getDeck(deckId)?.also { deck ->
                _editDeckState.value = editDeckState.value.copy(
                    deckName = deck.name,
                    imageUrl = deck.imageSource,
                    mainDeck = deck.mainDeck,
                    sideboard = deck.sideboard,
                )
            }
        }
    }

    fun onEvent(event: EditDeckEvent) {
        when (event) {
            is EditDeckEvent.AddCard -> {
                viewModelScope.launch {
                    val card = cardsUseCases.getCardByName(event.cardName)

                    assert(event.type == null)
                    val target = event.type?: if (addCardMenuState.value.selectedDeckTypeId == 0) {
                        Deck.ListType.MainDeck
                    } else {
                        Deck.ListType.Sideboard
                    }

                    card?.let {
                        println(it.name)
                        when (target) {
                            Deck.ListType.MainDeck ->
                                _editDeckState.value = editDeckState.value.copy(
                                    mainDeck = _editDeckState.value.mainDeck
                                        .toMutableDeckList()
                                        .apply { addCard(card) }
                                        .toDeckList()
                                )
                            Deck.ListType.Sideboard ->
                                _editDeckState.value = editDeckState.value.copy(
                                    sideboard = _editDeckState.value.sideboard
                                        .toMutableDeckList()
                                        .apply { addCard(card) }
                                        .toDeckList()
                                )
                        }

                        _addCardMenuState.value = addCardMenuState.value.copy(
                            searchQuery = ""
                        )
                    }
                }
            }

            is EditDeckEvent.RemoveCard -> {
                viewModelScope.launch {
                    val card = cardsUseCases.getCardByName(event.cardName)
                    card?.let {
                        when (event.type) {
                            Deck.ListType.MainDeck ->
                                _editDeckState.value = editDeckState.value.copy(
                                    mainDeck = _editDeckState.value.mainDeck
                                        .toMutableDeckList()
                                        .apply { removeCard(card) }
                                        .toDeckList()
                                )
                            Deck.ListType.Sideboard ->
                                _editDeckState.value = editDeckState.value.copy(
                                    sideboard = _editDeckState.value.sideboard
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
                    id = editDeckState.value.deckId,
                    name = editDeckState.value.deckName,
                    imageSource = editDeckState.value.imageUrl,
                    mainDeck = editDeckState.value.mainDeck,
                    sideboard = editDeckState.value.sideboard,
                )

                println(deck.imageSource)

                viewModelScope.launch {
                    val id = deckUseCases.saveDeck(deck)
                    _editDeckState.value = editDeckState.value.copy(
                        deckId = id
                    )
                }
            }

            is EditDeckEvent.DeleteDeck -> {
                viewModelScope.launch {
                    deckUseCases.deleteDeckUseCase(editDeckState.value.deckId)
                }
            }

            is EditDeckEvent.EnteredDeckName -> {
                _editDeckState.value = editDeckState.value.copy(deckName = event.name)
                println("name: ${editDeckState.value.deckName}")
            }

            is EditDeckEvent.ChangeDeckImage -> {
                viewModelScope.launch {
                    val card = cardsUseCases.getCardByName(event.cardName)
                    _editDeckState.value = editDeckState.value.copy(
                        imageUrl = card!!.cropImageSource
                    )
                }
            }
        }
    }

    fun onEvent(event: EditDeckEvent.AddCardEvent) {
        when (event) {
            is EditDeckEvent.AddCardEvent.CardSearch -> {
                _addCardMenuState.value = addCardMenuState.value.copy(
                    searchQuery = event.query
                )
                viewModelScope.launch {
                    _addCardMenuState.value = addCardMenuState.value.copy(
                        searchResults = cardsUseCases.getSearchResults(event.query)
                    )
                }
            }

            is EditDeckEvent.AddCardEvent.SelectTargetDeckListType -> {
                _addCardMenuState.value = addCardMenuState.value.copy(
                    selectedDeckTypeId = event.id
                )
            }

            EditDeckEvent.AddCardEvent.ToggleAddCardMenu -> {
                _addCardMenuState.value = addCardMenuState.value.copy(
                    isExpanded = !addCardMenuState.value.isExpanded
                )
            }
        }
    }

    fun onEvent(event: EditDeckEvent.ImportDeckEvent) {
        when (event) {
            is EditDeckEvent.ImportDeckEvent.ToggleImportPopup -> {
                _importDeckState.value = importDeckState.value.copy(
                    isVisible = !importDeckState.value.isVisible,
                    input = "",
                )
            }

            is EditDeckEvent.ImportDeckEvent.EnteredDeckImportValue -> {
                _importDeckState.value = importDeckState.value.copy(
                    input = event.input
                )
            }

            is EditDeckEvent.ImportDeckEvent.ImportDeck -> {
                val deckCode = importDeckState.value.input
                _importDeckState.value = importDeckState.value.copy(
                    isVisible = false,
                    input = "",
                )

                viewModelScope.launch {
                    try {
                        val deck = deckUseCases.importDeck(deckCode)
                        _editDeckState.value = editDeckState.value.copy(
                            mainDeck = deck.mainDeck,
                            sideboard = deck.sideboard,
                        )
                    } catch (e: IllegalArgumentException) {
                        println(e.message) // TODO: error popup
                    }
                }
            }
        }
    }

    fun onEvent(event: EditDeckEvent.ChooseImageEvent) {
        when (event) {
            is EditDeckEvent.ChooseImageEvent.ImageSearch -> {
                _chooseImageState.value = chooseImageState.value.copy(
                    searchQuery = event.query
                )
                viewModelScope.launch {
                    _chooseImageState.value = chooseImageState.value.copy(
                        searchResults = cardsUseCases.getSearchResults(event.query)
                    )
                }
            }
            EditDeckEvent.ChooseImageEvent.ToggleChooseImagePopup -> {
                _chooseImageState.value = chooseImageState.value.copy(
                    isVisible = !chooseImageState.value.isVisible
                )
            }
        }
    }
}