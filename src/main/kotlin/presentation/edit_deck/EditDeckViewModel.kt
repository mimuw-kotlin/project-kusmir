package presentation.edit_deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.Card
import domain.model.Deck
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
                name = "New deck",
                imageUrl = "https://cards.scryfall.io/normal/front/5/6/56ebc372-aabd-4174-a943-c7bf59e5028d.jpg?1562629953",
                mainDeckCardCountMap = mutableMapOf(),
                sideboardCardCountMap = mutableMapOf()
            )
            println("DONE1")

            deckUseCases.getDeck(deckId)?.also { deck ->
                _state.value = state.value.copy(
                    name = deck.name,
                    imageUrl = "https://cards.scryfall.io/normal/front/5/6/56ebc372-aabd-4174-a943-c7bf59e5028d.jpg?1562629953",
                    mainDeckCardCountMap = deck.mainDeck.toCardCountMap(),
                    sideboardCardCountMap = deck.sideboard.toCardCountMap()
                )
                println("DONE ${state.value.name}")
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
                    val target = event.type?: if (state.value.selectedDeckTypeId == 0) {
                        Deck.ListType.MainDeck
                    } else {
                        Deck.ListType.Sideboard
                    }

                    card?.let {
                        when (target) {
                            Deck.ListType.MainDeck ->
                                _state.value = state.value.copy(
                                    mainDeckCardCountMap = _state.value.mainDeckCardCountMap.addCard(card)
                                )
                            Deck.ListType.Sideboard ->
                                _state.value = state.value.copy(
                                    sideboardCardCountMap = _state.value.sideboardCardCountMap.addCard(card)
                                )
                        }

                        _state.value = state.value.copy(
                            cardsSearchQuery = ""
                        )
                    }
                }
            }

            is EditDeckEvent.EnteredDeckName -> {
                _state.value = state.value.copy(name = event.name)
                println("name: ${state.value.name}")
            }

            is EditDeckEvent.CardSearch -> {
                _state.value = state.value.copy(
                    cardsSearchQuery = event.query,
                )
                viewModelScope.launch {
                    _state.value = state.value.copy(
                        cardsSearchResults = cardsUseCases.getSearchResults(event.query)
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
                                    mainDeckCardCountMap = _state.value.mainDeckCardCountMap.removeCard(card)
                                )
                            Deck.ListType.Sideboard ->
                                _state.value = state.value.copy(
                                    sideboardCardCountMap = _state.value.sideboardCardCountMap.removeCard(card)
                                )
                        }
                    }
                }
            }

            is EditDeckEvent.SaveDeck -> {
                val deck = Deck(
                    id = state.value.deckId,
                    name = state.value.name,
                    mainDeck = state.value.mainDeckCardCountMap.flatMap {
                        (key, count) -> List(count) { key }
                    },
                    sideboard = state.value.sideboardCardCountMap.flatMap {
                        (key, count) -> List(count) { key }
                    }
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
                    isAddCardMenuExpanded = !state.value.isAddCardMenuExpanded
                )
            }

            is EditDeckEvent.SelectTargetDeckListType -> {
                _state.value = state.value.copy(
                    selectedDeckTypeId = event.id
                )
            }
        }
    }

    private fun List<Card>.toCardCountMap(): MutableMap<Card, Int> {
        return this.groupingBy { it }.eachCount().toMutableMap()
    }

    private fun MutableMap<Card, Int>.toList(): List<Card> {
        return this.flatMap { (card, count) -> List(count) { card } }
    }

    private fun MutableMap<Card, Int>.addCard(card: Card): MutableMap<Card, Int> {
        return this.toMutableMap().apply {
            this[card] = (this[card] ?: 0) + 1
        }
    }

    private fun MutableMap<Card, Int>.removeCard(card: Card): MutableMap<Card, Int> {
        return this.toMutableMap().apply {
            if (this[card] == 1) {
                this.remove(card)
            } else this[card] = this[card]!! - 1
        }
    }
}