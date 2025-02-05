package presentation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import presentation.decks.DecksScreen
import presentation.decktracker.DeckTrackerScreen
import presentation.decktracker.DeckTrackerViewModel
import presentation.editdeck.EditDeckScreen
import presentation.home.HomeScreen
import presentation.statistics.StatisticsScreen
import java.awt.Dimension

sealed class Screen {
    abstract val name: String

    @Serializable
    data object Home : Screen() {
        override val name = "Home"
    }

    @Serializable
    data class EditDeck(
        val deckId: Long = -1,
    ) : Screen() {
        override val name = "Edit deck"
    }

    @Serializable
    data object Decks : Screen() {
        override val name = "Decks"
    }

    @Serializable
    data object Statistics : Screen() {
        override val name = "Statistics"
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
private fun MainWindowContent() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
    ) {
        composable<Screen.Home> {
            HomeScreen(
                navController = navController,
                viewModel = koinViewModel(),
            )
        }
        composable<Screen.Decks> {
            DecksScreen(
                navController = navController,
                viewModel = koinViewModel(),
            )
        }

        composable<Screen.Statistics> {
            StatisticsScreen(
                navController = navController,
                viewModel = koinViewModel(),
            )
        }

        composable<Screen.EditDeck> {
            val args = it.toRoute<Screen.EditDeck>()
            EditDeckScreen(
                navController = navController,
                viewModel = koinViewModel(parameters = { parametersOf(args.deckId) }),
            )
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ApplicationScope.MainApp() {
    class TrackerOverlayWrapper : KoinComponent {
        val trackerViewModel: DeckTrackerViewModel by inject()
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "mtgo-tracker",
    ) {
        window.minimumSize = Dimension(960, 540)
        MaterialTheme {
            MainWindowContent()
        }
    }

    val trackerViewModel = TrackerOverlayWrapper().trackerViewModel
    val trackerState by trackerViewModel.state.collectAsState()
    val isTrackerOverlayActive = trackerState.isWindowOpen
    if (isTrackerOverlayActive) {
        Window(
            onCloseRequest = {},
            alwaysOnTop = true,
        ) {
            MaterialTheme {
                DeckTrackerScreen(trackerViewModel)
            }
        }
    }
}
