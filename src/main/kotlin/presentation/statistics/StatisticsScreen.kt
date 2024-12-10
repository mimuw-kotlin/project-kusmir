package presentation.statistics

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import presentation.Screen
import presentation.common.components.CustomTopBar

@Composable
fun StatisticsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CustomTopBar(
                onBackPressed = { navController.navigateUp() },
                onNavigate = { screen -> navController.navigate(screen) },
                currentScreen = Screen.Statistics,
            )
        },
    ) {
        Text("Soon there will be stats here :)")
    }
}
