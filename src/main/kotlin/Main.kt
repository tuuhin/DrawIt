import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import presentation.CanvasActionsTopBar
import presentation.drawing.DrawingCanvas
import presentation.menu_options.MenuOptionsSheet
import presentation.style_picker.CanvasDrawStylePicker
import ui.DrawItAppTheme


fun main() = application {

    System.setProperty("skiko.renderApi", "OPENGL")

    val windowState = rememberWindowState()
    val viewModel = AppViewModel()

    Window(
        title = "DrawIt",
        state = windowState,
        onCloseRequest = ::exitApplication,
    ) {

        val actionBarState by viewModel.actionBarState.collectAsState()
        val drawStyle by viewModel.drawStyleState.collectAsState()
        val canvasDrawObjects by viewModel.canvasObjects.collectAsState()

        DrawItAppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                // this will be the canvas
                DrawingCanvas(
                    state = actionBarState,
                    drawnObjects = canvasDrawObjects,
                    style = drawStyle,
                    onCreateNewObject = viewModel::onAddNewObject,
                    modifier = Modifier.fillMaxSize()
                )
                // this will contain all the actions
                Box(
                    modifier = Modifier.fillMaxSize().padding(12.dp)
                ) {
                    // menu bar
                    MenuOptionsSheet(modifier = Modifier.align(Alignment.TopStart))
                    // draw style picker
                    CanvasDrawStylePicker(
                        style = drawStyle,
                        onEvent = viewModel::onDrawStyleChange,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    CanvasActionsTopBar(
                        state = actionBarState,
                        onActionChange = viewModel::onActionBarAction,
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}
