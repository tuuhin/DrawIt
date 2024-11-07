import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.app_name
import com.eva.draw_it.drawit.generated.resources.ic_main
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.DrawItAppTheme


fun main() = application {

    System.setProperty("skiko.renderApi", "OPENGL")

    val windowState = rememberWindowState()

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.ic_main),
    ) {
        DrawItAppTheme {
            App()
        }
    }
}