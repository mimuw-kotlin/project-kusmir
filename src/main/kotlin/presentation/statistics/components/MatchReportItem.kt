package presentation.statistics.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.GameReport
import domain.model.GameResult
import domain.model.MatchReport
import presentation.common.components.RowSwitch
import presentation.util.noRippleClickable
import java.text.SimpleDateFormat
import java.util.*

private fun Date.toFormattedString(): String {
    val formatter = SimpleDateFormat("MMM dd HH:mm", Locale.getDefault())
    return formatter.format(this)
}

@Composable
fun MatchReportItem(
    matchReport: MatchReport,
    isExpanded: Boolean,
    onClick: () -> Unit,
    getDeckName: (Long) -> String,
) {
    Card(
        modifier =
            Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .noRippleClickable { onClick() }
                .animateContentSize(),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .pointerHoverIcon(PointerIcon.Hand),
            ) {
                listOf(
                    getDeckName(matchReport.registeredDeckId),
                    matchReport.date.toFormattedString(),
                    matchReport.format.toString(),
                    matchReport.structure.toString(),
                    matchReport.opponentName,
                ).forEach { text ->
                    Text(
                        text = text.ifBlank { "???" },
                        modifier =
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                    )
                }

                val wonGames = matchReport.gameReports.filter { it.result == GameResult.WON }.size
                val lostGames = matchReport.gameReports.filter { it.result == GameResult.LOST }.size
                Text(
                    text = "$wonGames - $lostGames",
                    modifier =
                        Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    color =
                        if (wonGames > lostGames) {
                            Color.Green
                        } else if (wonGames < lostGames) {
                            Color.Red
                        } else {
                            Color.Gray
                        },
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                GameReportTabs(gameReports = matchReport.gameReports)
            }
        }
    }
}

@Composable
fun GameReportTabs(gameReports: List<GameReport>) {
    var selectedIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        RowSwitch(
            selectedIndex = selectedIndex,
            items = (1..gameReports.size).map { "Game $it" },
            onSelectionChange = { selectedIndex = it },
        )

        Spacer(modifier = Modifier.height(8.dp))
        GameReportDetails(gameReports[selectedIndex])
    }
}

@Composable
fun GameReportDetails(gameReport: GameReport) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        when (gameReport.result) {
            GameResult.WON -> Text("WON", color = Color.Green)
            GameResult.DRAW -> Text("DRAW", color = Color.Gray)
            GameResult.LOST -> Text("LOST", color = Color.Red)
        }
        Text("On the Play: ${gameReport.isOnThePlay}")
        Text("Player Mulligan: ${gameReport.playerMulligan}")
        Text("Opponent Mulligan: ${gameReport.opponentMulligan}")

        Row {
            if (gameReport.cardsSidedIn.isNotEmpty() || gameReport.cardsSidedOut.isNotEmpty()) {
                Text("Sideboarding: ")
                gameReport.cardsSidedIn
                    .groupingBy { it.name }
                    .eachCount()
                    .forEach { (name, count) ->
                        Text(
                            text = "+$count $name ",
                            color = Color.Green,
                        )
                    }

                gameReport.cardsSidedOut
                    .groupingBy { it.name }
                    .eachCount()
                    .forEach { (name, count) ->
                        Text(
                            text = "-$count $name ",
                            color = Color.Red,
                        )
                    }
            }
        }
    }
}
