import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import presentation.CanvasActionsTopBar
import presentation.drawing.DrawingCanvas
import presentation.menu_options.MenuOptionsSheet
import presentation.menu_options.ZoomAndUndoRedoOption
import presentation.style_picker.CanvasDrawStylePicker

@Composable
fun App(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {

    val snackBarState = remember { SnackbarHostState() }

    val actionBarState by viewModel.actionBarState.collectAsState()
    val drawStyle by viewModel.drawStyleState.collectAsState()
    val canvasDrawObjects by viewModel.canvasObjects.collectAsState()
    val canvasProperties by viewModel.canvasProperties.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
    ) {
        // this will be the canvas
        DrawingCanvas(
            state = actionBarState,
            propertiesState = canvasProperties,
            drawnObjects = canvasDrawObjects,
            style = drawStyle,
            onInteractionEvent = viewModel::onCanvasItemEvent,
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
            AnimatedVisibility(
                visible = actionBarState.hasAction,
                modifier = Modifier.align(Alignment.CenterStart),
                enter = slideInHorizontally { width -> -width } + scaleIn(
                    transformOrigin = TransformOrigin(pivotFractionX = 0f, pivotFractionY = .5f)
                ),
                exit = slideOutHorizontally { width -> -width } + scaleOut(
                    transformOrigin = TransformOrigin(pivotFractionX = 0f, pivotFractionY = .5f)
                )
            ) {
                CanvasDrawStylePicker(
                    style = drawStyle,
                    onEvent = viewModel::onDrawStyleChange,
                )
            }
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