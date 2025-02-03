package presentation.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import domain.model.GameResult
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.Screen
import presentation.common.components.CustomTopBar
import presentation.statistics.components.MatchReportItem
import presentation.statistics.components.PercentageCircle

@OptIn(KoinExperimentalAPI::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = koinViewModel()
) {
    val state: StatisticsState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CustomTopBar(
                onNavigate = { screen -> navController.navigate(screen) },
                currentScreen = Screen.Statistics,
            )
        },
    ) {
        var expandedIndex by remember { mutableStateOf(-1L) }

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.3f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val wonMatches = state.matchReports.filter { report ->
                    val wonGames = report.gameReports.filter { it.result == GameResult.WON }.size
                    val lostGames = report.gameReports.filter { it.result == GameResult.LOST }.size
                    wonGames > lostGames
                }

                val winRatio =
                    if (state.matchReports.isEmpty()) {
                        0f
                    } else {
                        wonMatches.size / state.matchReports.size.toFloat()
                    }

                Column {
                    PercentageCircle(winRatio)
                    Text("Match Winrate")
                }
            }

            Column(modifier = Modifier.weight(0.7f).padding(16.dp)) {
                Text(
                    text = "Recent games",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5
                )
                HeaderRow()
                LazyColumn {
                    items(state.matchReports) { report ->
                        MatchReportItem(
                            matchReport = report,
                            isExpanded = expandedIndex == report.id,
                            onClick = {
                                expandedIndex = if (expandedIndex == report.id) -1L else report.id
                            },
                            getDeckName = { deckId ->
                                state.decks.find { it.id == deckId }?.name ?: "???"
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderRow() {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        listOf("Deck", "Date", "Format", "Structure", "Opponent", "Result").forEach { label ->
            Text(
                text = label,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
