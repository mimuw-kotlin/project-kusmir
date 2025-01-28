package domain.model

import java.util.Date

data class MatchReport(
    val id: Long,
    val opponentName: String,
    val date: Date,
    val structure: Structure,
    val format: MtgFormat,
    val registeredDeckId: Long,
    val gameReports: List<GameReport>
) {
    enum class Structure {
        Bo1,
        Bo3,
    }
}
