package presentation.editdeck

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import domain.model.Deck
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.Screen
import presentation.common.components.CustomTooltip
import presentation.common.components.CustomTopBar
import presentation.decks.components.DeckItem
import presentation.editdeck.components.AddCardMenu
import presentation.editdeck.components.CardItem
import presentation.editdeck.components.ChooseImagePopup
import presentation.editdeck.components.ImportDeckPopup
import presentation.editdeck.util.groups

@OptIn(KoinExperimentalAPI::class, ExperimentalFoundationApi::class)
@Composable
fun EditDeckScreen(
    navController: NavController,
    viewModel: EditDeckViewModel = koinViewModel(),
) {
    val editDeckState by viewModel.editDeckState.collectAsState()
    val addCardMenuState by viewModel.addCardMenuState.collectAsState()
    val chooseImageState by viewModel.chooseImageState.collectAsState()
    val importDeckState by viewModel.importDeckState.collectAsState()

    Scaffold(
        topBar = {
            CustomTopBar(
                onBackPressed = { navController.navigateUp() },
                onNavigate = { screen -> navController.navigate(screen) },
                currentScreen = Screen.Decks,
            )
        },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            // Left Column for cards
            Column(
                modifier =
                    Modifier
                        .weight(0.7f)
                        .padding(4.dp),
            ) {
                Row(modifier = Modifier.weight(0.7f)) {
                    // Main Deck
                    Card(
                        modifier =
                            Modifier
                                .weight(0.66f)
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(Color.LightGray),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .background(Color.Transparent),
                        ) {
                            Text("Main Deck (${editDeckState.mainDeck.totalSize})", style = MaterialTheme.typography.h5)
                            LazyColumn {
                                for ((groupName, cards) in editDeckState.mainDeck.groups()) {
                                    item {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "$groupName (${cards.totalSize})",
                                            style =
                                                TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                        )
                                    }

                                    items(cards.toList()) { (card, count) ->
                                        CardItem(
                                            count = count,
                                            cardName = card.name,
                                            cardImageUrl = card.imageSource,
                                            onAdd = {
                                                viewModel.onEvent(EditDeckEvent.AddCard(it, Deck.ListType.MainDeck))
                                            },
                                            onRemove = {
                                                viewModel.onEvent(EditDeckEvent.RemoveCard(it, Deck.ListType.MainDeck))
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sideboard
                    Card(
                        modifier =
                            Modifier
                                .weight(0.33f)
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(Color.LightGray),
                    ) {
                        LazyColumn(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .background(Color.Transparent),
                        ) {
                            item {
                                Text("Sideboard", style = MaterialTheme.typography.h5)
                            }
                            items(editDeckState.sideboard.toList()) { (card, count) ->
                                CardItem(
                                    count = count,
                                    cardName = card.name,
                                    cardImageUrl = card.imageSource,
                                    onAdd = {
                                        viewModel.onEvent(
                                            EditDeckEvent.AddCard(
                                                cardName = it,
                                                type = Deck.ListType.Sideboard,
                                            ),
                                        )
                                    },
                                    onRemove = {
                                        viewModel.onEvent(
                                            EditDeckEvent.RemoveCard(
                                                cardName = it,
                                                type = Deck.ListType.Sideboard,
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
                modifier =
                    Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                        .widthIn(min = 100.dp, max = 240.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DeckItem(
                    deckName = editDeckState.deckName,
                    onDeckNameChanged = { viewModel.onEvent(EditDeckEvent.EnteredDeckName(it)) },
                    deckImageUrl = editDeckState.imageUrl,
                )

                Spacer(Modifier.height(8.dp))

                AddCardMenu(
                    isExpanded = addCardMenuState.isExpanded,
                    onExpand = {
                        viewModel.onEvent(EditDeckEvent.AddCardEvent.ToggleAddCardMenu)
                    },
                    cardQueryText = addCardMenuState.searchQuery,
                    cardQueryResults = addCardMenuState.searchResults,
                    onCardSearch = {
                        viewModel.onEvent(EditDeckEvent.AddCardEvent.CardSearch(it))
                    },
                    onCardSelected = {
                        viewModel.onEvent(EditDeckEvent.AddCard(it))
                    },
                    onDeckTypeSelected = {
                        viewModel.onEvent(EditDeckEvent.AddCardEvent.SelectTargetDeckListType(it))
                    },
                    selectedDeckListTypeId = addCardMenuState.selectedDeckTypeId,
                )

                Spacer(Modifier.weight(1f))

                Row(modifier = Modifier.weight(0.3f)) {
                    TooltipArea(
                        tooltip = { CustomTooltip("Delete deck") },
                        modifier = Modifier.weight(0.33f).pointerHoverIcon(PointerIcon.Hand),
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(EditDeckEvent.DeleteDeck)
                                navController.navigateUp()
                            },
                            modifier =
                                Modifier
                                    .weight(0.33f),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete deck",
                                modifier =
                                    Modifier
                                        .fillMaxSize(0.8f),
                            )
                        }
                    }

                    TooltipArea(
                        tooltip = { CustomTooltip("Save deck") },
                        modifier = Modifier.weight(0.33f).pointerHoverIcon(PointerIcon.Hand),
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(EditDeckEvent.EnteredDeckName(editDeckState.deckName))
                                viewModel.onEvent(EditDeckEvent.SaveDeck)
                                navController.navigateUp()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save deck",
                                modifier =
                                    Modifier
                                        .fillMaxSize(0.8f),
                            )
                        }
                    }

                    TooltipArea(
                        tooltip = { CustomTooltip("Choose deck image") },
                        modifier = Modifier.weight(0.33f).pointerHoverIcon(PointerIcon.Hand),
                    ) {
                        IconButton(
                            onClick = {
                                if (!chooseImageState.isVisible) {
                                    viewModel.onEvent(EditDeckEvent.ChooseImageEvent.ToggleChooseImagePopup)
                                }
                            },
                            modifier =
                                Modifier
                                    .weight(0.33f),
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Choose deck image",
                                modifier =
                                    Modifier
                                        .fillMaxSize(0.8f),
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (!importDeckState.isVisible) {
                            viewModel.onEvent(EditDeckEvent.ImportDeckEvent.ToggleImportPopup)
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Text("Import decklist")
                }
            }
        }

        ImportDeckPopup(
            isVisible = importDeckState.isVisible,
            input = importDeckState.input,
            onClickOutside = { viewModel.onEvent(EditDeckEvent.ImportDeckEvent.ToggleImportPopup) },
            onValueChange = {
                viewModel.onEvent(EditDeckEvent.ImportDeckEvent.EnteredDeckImportValue(it))
            },
            onSubmit = { viewModel.onEvent(EditDeckEvent.ImportDeckEvent.ImportDeck) },
            modifier =
                Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.8f),
        )

        ChooseImagePopup(
            isVisible = chooseImageState.isVisible,
            query = chooseImageState.searchQuery,
            results = chooseImageState.searchResults,
            onClickOutside = {
                viewModel.onEvent(EditDeckEvent.ChooseImageEvent.ToggleChooseImagePopup)
            },
            onSearch = {
                viewModel.onEvent(EditDeckEvent.ChooseImageEvent.ImageSearch(it))
            },
            onSubmit = {
                viewModel.onEvent(EditDeckEvent.ChangeDeckImage(it))
                viewModel.onEvent(EditDeckEvent.ChooseImageEvent.ToggleChooseImagePopup)
            },
            modifier =
                Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.8f),
        )
    }
}
