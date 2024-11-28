package presentation.decks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun DeckItem(
    deckName: String,
    deckImageUrl: String?,
    onDeckNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        // TODO: Change placeholder image
        val imageSource = deckImageUrl ?: "resources/img/MausoleumWanderer.jpg"
        AsyncImage(
            model = imageSource,
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(10f / 7)
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp),
            contentScale = ContentScale.Crop,
        )

        BasicTextField(
            value = deckName,
            onValueChange = { newText ->
                onDeckNameChanged(newText)
            },
            textStyle =
                TextStyle(
                    fontSize = 24.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                ),
            singleLine = true,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (8).dp)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f)),
        )
    }
}
