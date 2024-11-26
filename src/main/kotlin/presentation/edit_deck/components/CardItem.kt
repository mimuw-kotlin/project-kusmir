package presentation.edit_deck.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardItem (
    count: Int,
    cardName: String,
    cardImageUrl: String,
    onAdd: (String) -> Unit = {},
    onRemove: (String) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = count.toString(),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        TooltipArea(
            tooltip = {
                AsyncImage(
                    model = cardImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(298.dp)
                        .aspectRatio(745f / 1040f) // Playing card aspect ratio
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        ) {
            Text(
                text = cardName,
                style = TextStyle(
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .pointerHoverIcon(PointerIcon.Hand),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row {
            val onAddInteractionSource = remember { MutableInteractionSource() }
            Icon(
                Icons.Default.Add,
                contentDescription = "Add a card",
                tint = lerp(Color.Green, Color.Black, 0.2f),
                modifier = Modifier
                    .width(24.dp)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable(
                        interactionSource = onAddInteractionSource,
                        indication = ripple(
                            radius = 12.dp
                        ),
                    ) { onAdd(cardName) }
            )

            val onRemoveInteractionSource = remember { MutableInteractionSource() }
            Icon(
                Icons.Default.Remove,
                contentDescription = "Remove a card",
                tint = lerp(Color.Red, Color.Black, 0.2f),
                modifier = Modifier
                    .width(24.dp)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable(
                        interactionSource = onRemoveInteractionSource,
                        indication = ripple(
                            radius = 12.dp
                        ),
                    ) { onRemove(cardName) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewCardItem() {
    CardItem(
        count = 4,
        cardName = "Lightning Bolt",
        cardImageUrl = "" // the image will not be displayed anyways
    )
}