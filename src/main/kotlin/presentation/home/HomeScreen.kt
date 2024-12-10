package presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.Screen
import presentation.common.components.CustomTopBar
import presentation.common.components.LoadingSpinner

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state: HomeState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CustomTopBar(
                onBackPressed = { navController.navigateUp() },
                onNavigate = { screen -> navController.navigate(screen) },
                currentScreen = Screen.Home,
            )
        },
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = { viewModel.onEvent(HomeEvent.FetchCardsData) },
                    enabled = !state.isLoadingCards,
                ) {
                    Text("Fetch cards data")
                }

                Spacer(modifier = Modifier.width(8.dp))

                state.bulkFetchDate?.let {
                    val formattedDateString =
                        it.format(
                            LocalDateTime.Format {
                                byUnicodePattern("yyyy.MM.dd - HH:mm")
                            },
                        )
                    Text("Last sync: $formattedDateString")
                } ?: Text("No cards collection downloaded.")
            }

            if (state.isLoadingCards) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(modifier = Modifier.padding(vertical = 8.dp).weight(0.8f)) {
                        LoadingSpinner()
                    }
                    Row(modifier = Modifier.padding(vertical = 8.dp).weight(0.2f)) {
                        Text("Fetching cards data...")
                    }
                }
            }
        }
    }
}
