package presentation.edit_deck.components

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import presentation.components.DefaultTextField
import presentation.components.PopupBox

@Composable
fun ImportDeckPopup(
    isVisible: Boolean,
    input: String,
    modifier: Modifier,
    onClickOutside: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    PopupBox(
        visible = isVisible,
        onClickOutside = onClickOutside,
        content = {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Paste the deck here")

                    DefaultTextField(
                        value = input,
                        onValueChange = { onValueChange(it) },
                        singleLine = false,
                        placeholder = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(16.dp),
                    )
                }
            }
        }
    )
}
