package presentation.decks

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.Screen
import presentation.common.components.CustomTopBar
import presentation.decks.components.DeckItem

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun DecksScreen(
    navController: NavController,
    viewModel: DecksViewModel = koinViewModel<DecksViewModel>(),
) {
    val state = viewModel.state

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.EditDeck())
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add new deck")
            }
        },
        topBar = {
            CustomTopBar(
                onBackPressed = {},
                onNavigate = { screen -> navController.navigate(screen) },
                currentScreen = Screen.Decks,
            )
        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.width(1000.dp),
        ) {
            items(state.value.decks) { deck ->
                DeckItem(
                    deckName = deck.name,
                    deckImageUrl = deck.imageSource,
                    onDeckNameChanged = {},
                    modifier =
                        Modifier
                            .clickable(
                                interactionSource =
                                    remember {
                                        MutableInteractionSource()
                                    },
                                indication = null,
                                onClick = {
                                    navController.navigate(Screen.EditDeck(deck.id))
                                },
                            ),
                )
            }
        }
    }
}
