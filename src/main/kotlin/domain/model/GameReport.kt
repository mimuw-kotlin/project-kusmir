package domain.model

enum class GameResult {
    WON,
    LOST,
    DRAW,
}

data class GameReport(
    val result: GameResult,
    val isOnThePlay: Boolean, // true, if player starts the game, false if opponent is going first
    val opponentRevealedCards: List<Card>,
    val playerDrawnCards: List<Card>,
    val playerMulligan: Int,
    val opponentMulligan: Int,
    val cardsSidedOut: List<Card>,
    val cardsSidedIn: List<Card>,
)
