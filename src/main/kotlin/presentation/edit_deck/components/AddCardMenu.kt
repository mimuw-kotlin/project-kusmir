package presentation.edit_deck.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.components.SearchBar
import presentation.edit_deck.EditDeckEvent

const val ANIMATION_DURATION = 300

@Composable
fun ExpandableContent(
    isExpanded: Boolean,
    content: @Composable () -> Unit
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
    val existTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

    }

    AnimatedVisibility(
        visible = isExpanded,
        enter = enterTransition,
        exit = existTransition
    ) {
        content()
    }
}

@Composable
fun AddCardMenu(
    isExpanded: Boolean,
    onExpand: () -> Unit,
    cardQueryText: String,
    cardQueryResults: List<String>,
    onCardSearch: (String) -> Unit,
    onCardSelected: (String) -> Unit,
    selectedDeckListTypeId: Int,
    onDeckTypeSelected: (Int) -> Unit,
) {
    val transition = updateTransition(targetState = isExpanded, label = "transition")

    val iconRotationAngle by
            transition.animateFloat(label = "change icon") { state ->
                if (state) 0f else 180f
            }

    Card(backgroundColor = MaterialTheme.colors.surface) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clickable {
                    onExpand()
                }
            ) {
                Text("Add card")

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(iconRotationAngle)
                )
            }

            ExpandableContent(
                isExpanded = isExpanded,
                content = {
                    Column {
                        RowSwitch(
                            selectedIndex = selectedDeckListTypeId,
                            items = listOf("Main Deck", "Sideboard"),
                            onSelectionChange = onDeckTypeSelected
                        )
                        SearchBar(
                            query = cardQueryText,
                            results = cardQueryResults,
                            onValueChange = onCardSearch,
                            onItemSelected = onCardSelected,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                            ),
                        )
                    }

                }
            )
        }
    }
}