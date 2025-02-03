package domain.usecases.tracking

import java.io.File

private fun getMtgoRootDirectory(): File {
    val userName = System.getProperty("user.name")
    return File("C:\\Users\\$userName\\AppData\\Local\\Apps\\2.0")
}

private fun getMatchLogFile(): File {
    /*
        Here again we're doing some educated guessing on the file location.
        According to personal tests, the path patterns follow:
            [mtgoRootDirectory]\
                Data\
                    [random characters]\
                        [more random characters]\
                            mtgo..tion_*\
                                Data\
                                    AppFiles\
                                        \[random characters]
                                            \Match_GameLog_*

        Each game started by client produces a log file of following path.
        To get the current match log we traverse the directory and look for the newest file.
     */

    return getMtgoRootDirectory()
        .walk()
        .filter {
            it.isFile && it.name.startsWith("Match_GameLog_")
        }.maxByOrNull { file ->
            file.lastModified()
        } ?: error("No match logs found")
}

data class ParsedGameResult(
    val winner: String,
    val startingPlayer: String,
    val handSizes: Map<String, Int>,
)

class ParseMatchLogUseCase {
    operator fun invoke(
        // By default, look for the most recent MatchLog file.
        logFile: File = getMatchLogFile(),
    ): List<ParsedGameResult> {
        val log = logFile.readText()

        val firstPlayerRegex = "@P(?<player>\\S+) chooses to play first".toRegex()
        val handSizeRegex = "@P(?<player>\\S+)( put.*)? begins the game with (?<handsize>[a-z]+) cards in hand".toRegex()

        val games = log.split(Regex("wins the game|loses the game")).dropLast(1)

        return games.map { game ->
            val outcome = Regex("@P(?<player>\\S+) (wins|loses) the game").find(log, log.indexOf(game))
            val winner =
                if (outcome?.value?.contains("wins") == true) {
                    outcome.groups["player"]?.value ?: "Unknown"
                } else {
                    // If it's a "loses" message, infer the winner based on the other player.
                    val loser = outcome?.groups?.get("player")?.value ?: "Unknown"
                    val otherPlayers =
                        handSizeRegex.findAll(game)
                            .map { it.groups["player"]?.value }
                            .filterNot { it == loser }
                            .toList()
                    otherPlayers.firstOrNull() ?: "Unknown"
                }

            val firstPlayerMatch = firstPlayerRegex.find(game)
            val startingPlayer = firstPlayerMatch?.groups?.get("player")?.value ?: "Unknown"

            val handSizes =
                handSizeRegex.findAll(game)
                    .map {
                        val player = it.groups["player"]?.value ?: "Unknown"
                        val handsize = it.groups["handsize"]?.value.fromWordToInt()!!
                        player to handsize
                    }
                    .toMap()

            ParsedGameResult(
                winner = winner,
                startingPlayer = startingPlayer,
                handSizes = handSizes,
            )
        }
    }
}

private fun String?.fromWordToInt(): Int? =
    when (this) {
        "zero" -> 0
        "one" -> 1
        "two" -> 2
        "three" -> 3
        "four" -> 4
        "five" -> 5
        "six" -> 6
        "seven" -> 7
        else -> null
    }
