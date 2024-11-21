import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.initKoin
import kotlinx.coroutines.runBlocking
import presentation.MainApp
import java.awt.Dimension

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainApp()
    }
}

fun main() = runBlocking {
    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "mtgo-tracker"
        ) {
            window.minimumSize = Dimension(960, 540)
            App()
        }
    }
}