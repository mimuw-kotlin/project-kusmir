package presentation.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTooltip(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White, shape = RectangleShape)
            .border(BorderStroke(1.dp, Color.Black), shape = RectangleShape)
            .padding(horizontal = 3.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = LocalTextStyle.current.copy(color = Color.Black, fontSize = 12.sp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun PreviewIconButtonsWithTooltips() {
    CustomTooltip("I'm a tooltip")
}