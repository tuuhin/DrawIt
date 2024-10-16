import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main() = application {

    System.setProperty("skiko.renderApi", "OPENGL")

    val windowState = rememberWindowState(placement = WindowPlacement.Floating, isMinimized = false)

    Window(onCloseRequest = ::exitApplication, state = windowState, title = "DrawIt") {

    }
}
