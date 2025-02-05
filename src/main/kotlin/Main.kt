import androidx.compose.ui.window.application
import di.initKoin
import kotlinx.coroutines.runBlocking
import presentation.MainApp

fun main() =
    runBlocking {
        initKoin()

        application {
            MainApp()
        }
    }
