package presentation.decktracker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import domain.model.Card
import kotlinx.coroutines.launch
import presentation.decktracker.components.CardItem

@Composable
fun DeckTrackerScreen(viewModel: DeckTrackerViewModel) {
    val state by viewModel.state.collectAsState()

    val previousQuantities = remember { mutableStateOf(mutableMapOf<Card, Int>()) }
    val animations = remember { mutableMapOf<Card, Animatable<Float, AnimationVector1D>>() }

    LaunchedEffect(state.cardsLeftInDeck) {
        state.cardsLeftInDeck.forEach { (card, newQuantity) ->
            val previousQuantity = previousQuantities.value[card] ?: 0
            if (newQuantity == previousQuantity) {
                return@forEach
            }

            val animatable = animations.getOrPut(card) { Animatable(0f) }

            animatable.stop()
            launch {
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1000),
                )

                animatable.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 1000),
                )
            }

            previousQuantities.value[card] = newQuantity
        }

        animations.keys.retainAll(state.cardsLeftInDeck.keys)
        previousQuantities.value.keys.retainAll(state.cardsLeftInDeck.keys)
    }

    Scaffold {
        Column {
            // items(state.cardsLeftInDeck.entries.toList()) { (card, quantity) ->
            state.cardsLeftInDeck.forEach { (card, quantity) ->
                val animationProgress = animations[card]?.value ?: 0f
                CardItem(
                    currentQuantity = quantity,
                    totalQuantity = state.registeredDeck?.mainDeck?.get(card) ?: 0,
                    cardImageUrl = card.imageSource,
                    highlightProgress = animationProgress,
                )
            }
        }
    }
}
