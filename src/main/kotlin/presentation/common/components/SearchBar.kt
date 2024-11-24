package presentation.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun SearchBar(
    query: String,
    results: List<String>,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search...",
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = true,
    onFocusChange: (FocusState) -> Unit = {},
    onItemSelected: (String) -> Unit = {}
) {
    var hoveredItemIndex by remember { mutableStateOf(-1) }
    val highlightedItemIndex = if (hoveredItemIndex == -1) 0 else hoveredItemIndex

    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier) {
        DefaultTextField(
            value = query,
            onValueChange = onValueChange,
            singleLine = singleLine,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            placeholder = placeholder,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .onFocusChanged { onFocusChange(it) }
                .onKeyEvent {
                    if (results.isNotEmpty() && it.key == Key.Enter) {
                        onItemSelected(results[highlightedItemIndex])
                        true
                    } else {
                        false
                    }
                }
        )
    }

    if (query.isNotBlank() && results.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(results.indices.toList()) { index ->
                val result = results[index]
                val isHovered by interactionSource.collectIsHoveredAsState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hoverable(interactionSource)
                        .background(
                            if (isHovered) Color.LightGray else Color.White,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            if(isHovered) { onItemSelected(result) }
                        }
                ) {
                    Text(
                        text = result,
                        color = if (isHovered) Color.Black else Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}