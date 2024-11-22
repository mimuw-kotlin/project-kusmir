package presentation.edit_deck

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import domain.model.Deck
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.components.DeckItem
import presentation.components.DefaultTextField
import presentation.components.PopupBox
import presentation.edit_deck.components.AddCardMenu
import presentation.edit_deck.components.CardItem
import presentation.edit_deck.components.ImportDeckPopup

@OptIn(KoinExperimentalAPI::class)
@Composable
fun EditDeckScreen(
    viewModel: EditDeckViewModel = koinViewModel(),
    deckId: Long?
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        content = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Left Column for cards
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(4.dp)
                ) {
                    Row(modifier = Modifier.weight(0.7f)) {
                        // Main Deck
                        Card(
                            modifier = Modifier
                                .weight(0.66f)
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(Color.LightGray)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .background(Color.Transparent)
                            ) {
                                item {
                                    Text("Main Deck", style = MaterialTheme.typography.h5)
                                }
                                items(state.mainDeckCardCountMap.toList()) { (card, count) ->
                                    CardItem(
                                        count = count,
                                        cardName = card.name,
                                        onAdd = {
                                            viewModel.onEvent(
                                                EditDeckEvent.AddCard(
                                                    cardName = it,
                                                    type = Deck.ListType.MainDeck
                                                ),
                                            )
                                        },
                                        onRemove = {
                                            viewModel.onEvent(
                                                EditDeckEvent.RemoveCard(
                                                    cardName = it,
                                                    type = Deck.ListType.MainDeck
                                                ),
                                            )
                                        },
                                    )
                                }
                            }
                        }

                        //Sideboard
                        Card(
                            modifier = Modifier
                                .weight(0.33f)
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(Color.LightGray)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .background(Color.Transparent)
                            ) {
                                item {
                                    Text("Sideboard", style = MaterialTheme.typography.h5)
                                }
                                items(state.sideboardCardCountMap.toList()) { (card, count) ->
                                    CardItem(
                                        count = count,
                                        cardName = card.name,
                                        onAdd = {
                                            viewModel.onEvent(
                                                EditDeckEvent.AddCard(
                                                    cardName = it,
                                                    type = Deck.ListType.Sideboard
                                                ),
                                            )
                                        },
                                        onRemove = {
                                            viewModel.onEvent(
                                                EditDeckEvent.RemoveCard(
                                                    cardName = it,
                                                    type = Deck.ListType.Sideboard
                                                ),
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.weight(0.3f)) {

                    }
                }

                // Right Column for Menu
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                        .widthIn(min = 100.dp, max = 240.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DeckItem(
                        deckName = state.name,
                        onDeckNameChanged = { viewModel.onEvent(EditDeckEvent.EnteredDeckName(it)) },
                        deckImageUrl = state.imageUrl,
                    )

                    Spacer(Modifier.height(8.dp))

                    AddCardMenu(
                        isExpanded = state.addCardMenuState.isExpanded,
                        onExpand = {
                            viewModel.onEvent(EditDeckEvent.ToggleAddCardMenu)
                        },
                        cardQueryText = state.addCardMenuState.searchBoxState.query,
                        cardQueryResults = state.addCardMenuState.searchBoxState.results,
                        onCardSearch = {
                            viewModel.onEvent(EditDeckEvent.CardSearch(it))
                        },
                        onCardSelected = {
                            viewModel.onEvent(EditDeckEvent.AddCard(it))
                        },
                        onDeckTypeSelected = {
                            viewModel.onEvent(EditDeckEvent.SelectTargetDeckListType(it))
                        },
                        selectedDeckListTypeId = state.addCardMenuState.selectedDeckTypeId
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            viewModel.onEvent(EditDeckEvent.EnteredDeckName(state.name))
                            viewModel.onEvent(EditDeckEvent.SaveDeck)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)

                    ) {
                        Text("Save Deck")
                    }

                    Button(
                        onClick = {
                            if (!state.importDeckState.isVisible)
                                viewModel.onEvent(EditDeckEvent.ToggleImportPopup)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)

                    ) {
                        Text("Import decklist")
                    }
                }
            }

            ImportDeckPopup(
                isVisible = state.importDeckState.isVisible,
                input = state.importDeckState.input,
                onClickOutside = { viewModel.onEvent(EditDeckEvent.ToggleImportPopup) },
                onValueChange = {
                    viewModel.onEvent(EditDeckEvent.EnteredDeckImportValue(it))
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.8f)
            )
        }
    )
}
