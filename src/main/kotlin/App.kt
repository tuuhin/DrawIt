import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.CanvasActionsTopBar
import presentation.drawing.DrawingCanvas
import presentation.menu_options.MenuOptionsSheet
import presentation.menu_options.ZoomAndUndoRedoOption
import presentation.style_picker.CanvasDrawStylePicker

@Composable
fun App() {

    val viewModel = remember { AppViewModel() }

    DisposableEffect(viewModel) {
        onDispose {
            // cleans the viewmodel
            viewModel.cleanUp()
        }
    }

    val snackBarState = remember { SnackbarHostState() }

    val actionBarState by viewModel.actionBarState.collectAsState()
    val drawStyle by viewModel.drawStyleState.collectAsState()
    val canvasDrawObjects by viewModel.canvasObjects.collectAsState()
    val canvasProperties by viewModel.canvasProperties.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        // this will be the canvas
        DrawingCanvas(
            state = actionBarState,
            propertiesState = canvasProperties,
            drawnObjects = canvasDrawObjects,
            style = drawStyle,
            onCreateNewObject = viewModel::onAddNewObject,
            onCanvasPropertiesEvent = viewModel::onCanvasPropertiesEvent,
            modifier = Modifier.fillMaxSize()
        )
        // this will contain all the actions
        Box(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            // menu bar
            MenuOptionsSheet(modifier = Modifier.align(Alignment.TopStart))
            // zoom options
            ZoomAndUndoRedoOption(
                properties = canvasProperties,
                onEvent = viewModel::onCanvasPropertiesEvent,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            // draw style picker
            CanvasDrawStylePicker(
                style = drawStyle,
                onEvent = viewModel::onDrawStyleChange,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            // top bar
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