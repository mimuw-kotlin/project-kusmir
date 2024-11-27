package presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import presentation.decks.DecksScreen
import presentation.editdeck.EditDeckScreen

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
fun MainApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Decks,
    ) {
        composable<Screen.Home> {
        }
        composable<Screen.Decks> {
            DecksScreen(
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
