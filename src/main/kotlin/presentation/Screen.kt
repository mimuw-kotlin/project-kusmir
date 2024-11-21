package presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import presentation.edit_deck.EditDeckScreen

sealed class Screen {
    @Serializable
    object Home
    @Serializable
    data class EditDeck(val deckId: Long = -1)
    @Serializable
    object Decks
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Decks
    ) {
        composable<Screen.Decks> {
           DecksScreen(
               navController = navController,
               viewModel = koinViewModel()
           )
        }

        composable<Screen.EditDeck> {
            val args = it.toRoute<Screen.EditDeck>()
            EditDeckScreen(
                viewModel = koinViewModel(parameters = { parametersOf(args.deckId) }),
                deckId = args.deckId
            )
        }
    }
}