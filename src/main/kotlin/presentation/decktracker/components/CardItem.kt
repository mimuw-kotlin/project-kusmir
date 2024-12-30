package presentation.decktracker.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import presentation.editdeck.components.PLAYING_CARD_ASPECT_RATIO
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardTextBar(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    val topBarRatio = 0.1f
    val topBarCutRatio = 0.055f
    val widthCutRatio = 0.055f

    Column(
        modifier
            .wrapContentHeight(
                unbounded = true,
                align = Alignment.Top,
            ).fillMaxWidth(),
    ) {
        TooltipArea(
            tooltip = {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .scale(0.75f)
                            .aspectRatio(PLAYING_CARD_ASPECT_RATIO)
                            .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )
            },
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Card Top Bar",
                contentScale = ContentScale.FillWidth,
                modifier =
                    Modifier
                        .clip(RectangleShape)
                        .layout { measurable, constraints ->
                            val placeable =
                                measurable.measure(constraints)

                            val borderWidth = (placeable.width * widthCutRatio).roundToInt()
                            val width = placeable.width - 2 * borderWidth
                            val height = (placeable.width * topBarRatio).roundToInt()

                            layout(width, height) {
                                val x = -borderWidth
                                val y = -(placeable.width * topBarCutRatio).roundToInt()
                                placeable.place(x, y)
                            }
                        },
            )
        }
    }
}

@Composable
fun CardItem(
    currentQuantity: Int,
    totalQuantity: Int,
    cardImageUrl: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(0.2f),
        ) {
            Text(
                "$currentQuantity / $totalQuantity",
                modifier = Modifier.align(Alignment.Center),
            )
        }
        CardTextBar(cardImageUrl, modifier = Modifier.weight(0.8f))
    }
}
