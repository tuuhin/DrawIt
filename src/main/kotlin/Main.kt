import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.DrawItAppTheme


fun main() = application {

    System.setProperty("skiko.renderApi", "OPENGL")

    val windowState = rememberWindowState()

    Window(
        title = "DrawIt",
        state = windowState,
        onCloseRequest = ::exitApplication,
    ) {
        DrawItAppTheme {
            App()
        }
    }
}