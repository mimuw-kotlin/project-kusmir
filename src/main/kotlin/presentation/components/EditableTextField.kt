package presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditableTextField(
    text: String = "",
    hint: String = "",
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isHintVisible: Boolean = false,
    onValueChange: (String) -> Unit = {},
    onFinishedEditing: (String) -> Unit = {},
    textStyle: TextStyle = TextStyle(),
    onFocusChange: (FocusState) -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
                .weight(1f)
                .height(30.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.2f)
                )
                .border(
                    BorderStroke(1.dp, if (isEditing) Color.Blue else Color.Transparent)
                )
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    onValueChange(newText)
                },
                singleLine = singleLine,
                textStyle = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .onFocusChanged {
                        onFocusChange(it)
                    },
                enabled = isEditing
            )
            if (isHintVisible) {
                Text(text = hint, style = textStyle)
            }
        }

        IconButton(onClick = {
            if (isEditing) onFinishedEditing(text)
            isEditing = !isEditing
        }) {
            Icon(
                imageVector = if (isEditing) Icons.Filled.Done else Icons.Filled.Edit,
                contentDescription = if (isEditing) "Done" else "Edit",
                tint = (if (isEditing) Color.Green else Color.Blue).copy(alpha = 0.5f)
            )
        }
    }
}

