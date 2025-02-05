package domain.usecases.tracking

import data.network.ScryfallApiImpl
import data.repository.CardsRepositoryImpl
import data.source.CardsDaoImpl
import kotlinx.coroutines.runBlocking
import util.mockCardDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ParseMatchLogUseCaseTest {
    @Test
    fun simpleParsingTest() {
        val trackingUseCases =
            TrackingUseCases(
                readLog = ReadLogUseCase(CardsRepositoryImpl(CardsDaoImpl(mockCardDatabase()), ScryfallApiImpl())),
                parseMatchLog = ParseMatchLogUseCase(),
            )

        val logFileName = "Match_GameLog_9e48dc5e-854f-44c7-a911-ae776517f830.dat"
        val logFile =
            File(
                this.javaClass.classLoader.getResource(logFileName)?.path
                    ?: error("Log file does not exist"),
            )

        val playerOne = "Kusmir"
        val playerTwo = "Legndary_PlaneSmoker"

        val expected =
            listOf(
                ParsedGameResult(
                    winner = playerTwo,
                    startingPlayer = playerTwo,
                    handSizes =
                        mapOf(
                            playerOne to 7,
                            playerTwo to 7,
                        ).toSortedMap(),
                ),
                ParsedGameResult(
                    winner = playerTwo,
                    startingPlayer = playerOne,
                    handSizes =
                        mapOf(
                            playerOne to 7,
                            playerTwo to 6,
                        ).toSortedMap(),
                ),
            )

        val results =
            runBlocking {
                trackingUseCases.parseMatchLog(logFile).map { result ->
                    result.copy(handSizes = result.handSizes.toSortedMap())
                }
            }

        assertEquals(expected, results)
    }
}
