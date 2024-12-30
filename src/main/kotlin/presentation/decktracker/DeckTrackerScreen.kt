package presentation.decktracker

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import presentation.decktracker.components.CardItem

@Composable
fun DeckTrackerScreen(viewModel: DeckTrackerViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold {
        Column {
            // items(state.cardsLeftInDeck.entries.toList()) { (card, quantity) ->
            state.cardsLeftInDeck.forEach { (card, quantity) ->
                CardItem(
                    currentQuantity = quantity,
                    totalQuantity = state.registeredDeck?.mainDeck?.get(card) ?: 0,
                    cardImageUrl = card.imageSource,
                )
            }
        }
    }
}
