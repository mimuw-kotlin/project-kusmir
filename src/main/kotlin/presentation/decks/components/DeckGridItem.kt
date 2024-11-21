package presentation.decks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.Deck
import org.jetbrains.skia.paragraph.TextBox
import presentation.components.EditableTextField

@Composable
fun DeckGridItem(
    deck: Deck
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .width(10.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .background(Color.Gray)
                        .height(120.dp)
                        .width(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Hopefully an image soon")
                }
                Column {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            tint = Color.Blue,
                            contentDescription = null
                        )
                    }
                }
            }
        }

    }
}