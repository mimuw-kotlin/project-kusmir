package presentation.statistics

import domain.model.Deck
import domain.model.MatchReport

data class StatisticsState(
    val matchReports: List<MatchReport> = emptyList(),
    val decks: List<Deck> = emptyList()
)