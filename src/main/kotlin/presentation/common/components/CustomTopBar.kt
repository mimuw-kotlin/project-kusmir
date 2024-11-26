package presentation.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.Screen

@Composable
fun CustomTopBar(
    onBackPressed: () -> Unit,
    onNavigate: (Screen) -> Unit,
    currentScreen: Screen
) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .background(MaterialTheme.colors.primarySurface)
    ) {
        IconButton(onClick = onBackPressed) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Spacer(Modifier.weight(1f))

        NavigationButtons(onNavigate, currentScreen)
    }
}

@Composable
fun NavigationButtons(
    onNavigate: (Screen) -> Unit,
    currentScreen: Screen,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        data class NavTarget(
            val icon: ImageVector,
            val screen: Screen,
        )

        val buttons = listOf(
            NavTarget(
                icon = Icons.Default.Home,
                screen = Screen.Home,
            ),
            NavTarget(
                icon = Icons.Default.Folder,
                screen = Screen.Decks,
            ),
            NavTarget(
                icon = Icons.Default.BarChart,
                screen = Screen.Statistics,
            ),
        )

        buttons.forEach { (icon, screen) ->
            val title = screen.name
            val isSelected = currentScreen == screen
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()

            val contentColor by animateColorAsState(
                targetValue = if (isSelected || isHovered) {
                    MaterialTheme.colors.onPrimary
                } else {
                    MaterialTheme.colors.onPrimary.copy(alpha = 0.6f)
                }
            )

            Button(
                onClick = { onNavigate(screen) },
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = contentColor
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun CustomTopBarPreview() {
    CustomTopBar(
        onBackPressed = {},
        onNavigate = {},
        currentScreen = Screen.Home
    )
}
