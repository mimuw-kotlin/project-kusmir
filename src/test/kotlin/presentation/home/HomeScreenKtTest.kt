import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import presentation.Screen
import presentation.home.HomeEvent
import presentation.home.HomeScreen
import presentation.home.HomeState
import presentation.home.HomeViewModel
import kotlin.test.Test

class HomeScreenTest {
    private val mockViewModel: HomeViewModel = mockk(relaxed = true)

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Fetch cards button is displayed and clickable`() =
        runComposeUiTest {
            every { mockViewModel getProperty "state" } returns MutableStateFlow(HomeState())

            setContent {
                HomeScreen(navController = rememberNavController(), viewModel = mockViewModel)
            }

            onNodeWithText("Fetch cards data").assertIsDisplayed()
            onNodeWithText("Fetch cards data").performClick()
            verify { mockViewModel.onEvent(HomeEvent.FetchCardsData) }
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Loading indicator is displayed during download`() =
        runComposeUiTest {
            every { mockViewModel getProperty "state" } returns MutableStateFlow(
                HomeState(isLoadingCards = true, downloadProgress = 0.5f)
            )

            setContent {
                HomeScreen(navController = rememberNavController(), viewModel = mockViewModel)
            }

            onNodeWithText("Fetching cards data...").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Navigation is enabled when not downloading`() =
        runComposeUiTest {
            every { mockViewModel getProperty "state" } returns MutableStateFlow(
                HomeState(isLoadingCards = false, downloadProgress = 0f)
            )

            val mockNavController: NavController = mockk(relaxed = true)

            setContent {
                HomeScreen(navController = mockNavController, viewModel = mockViewModel)
            }

            onNodeWithText("Home").performClick()
            verify { mockNavController.navigate(Screen.Home) }
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Navigation is disabled during download`() =
        runComposeUiTest {
            every { mockViewModel getProperty "state" } returns MutableStateFlow(
                HomeState(isLoadingCards = true, downloadProgress = 0.5f)
            )

            val mockNavController: NavController = mockk(relaxed = true)

            setContent {
                HomeScreen(navController = mockNavController, viewModel = mockViewModel)
            }

            onNodeWithText("Home").performClick()
            verify(exactly = 0) { mockNavController.navigate(any()) }
        }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Last fetch date is displayed when available`() =
        runComposeUiTest {
            val testDate = LocalDateTime(2025, 1, 28, 14, 41, 37)
            val testDateStr = "2025.01.28 - 14:41"

            val stateFlow = MutableStateFlow(HomeState())
            every { mockViewModel.state } returns stateFlow

            setContent {
                HomeScreen(navController = rememberNavController(), viewModel = mockViewModel)
            }

            onNodeWithText("Last sync: $testDateStr").assertDoesNotExist()

            stateFlow.value = HomeState(bulkFetchDate = testDate)

            onNodeWithText("Last sync: $testDateStr").assertIsDisplayed()
        }
}
