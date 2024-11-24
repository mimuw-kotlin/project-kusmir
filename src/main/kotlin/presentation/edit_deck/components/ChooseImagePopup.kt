package presentation.edit_deck.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.common.components.DefaultTextField
import presentation.common.components.PopupBox
import presentation.common.components.SearchBar

@Composable
fun ChooseImagePopup(
    isVisible: Boolean,
    query: String,
    results: List<String>,
    onClickOutside: () -> Unit,
    onSubmit: (String) -> Unit,
    onSearch : (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PopupBox(
        visible = isVisible,
        onClickOutside = onClickOutside,
        modifier = modifier,
        content = {
            Box(modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("Choose a card to represent your deck")
                    SearchBar(
                        query = query,
                        results = results,
                        onValueChange = onSearch,
                        onItemSelected = onSubmit,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                        )
                    )
                }
            }
        }
    )
}
