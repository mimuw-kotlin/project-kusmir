package presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex

@Composable
fun PopupBox(
    visible: Boolean,
    content: @Composable () -> Unit,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible) {
        val focusRequester = remember { FocusRequester() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .zIndex(10f),
            contentAlignment = Alignment.Center
        ) {
           Popup(
               alignment = Alignment.Center,
               onDismissRequest = { onClickOutside() },
               properties = PopupProperties(focusable = true),
           ) {
               Box(
                   modifier
                       .background(Color.White)
                       .clip(RoundedCornerShape(4.dp))
                       .focusRequester(focusRequester)
                       .focusable(),
                   contentAlignment = Alignment.Center
               ) {
                   content()
               }
           }
        }
    }
}