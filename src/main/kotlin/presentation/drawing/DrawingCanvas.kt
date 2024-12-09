package presentation.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import event.CanvasItemEvent
import event.CanvasPropertiesEvent
import models.*
import presentation.drawing.modifiers.*
import ui.DrawItAppTheme
import utils.thenIf
import java.util.*

@Composable
private fun DrawingCanvas(
    state: ActionBarState,
    style: CanvasDrawStyle,
    properties: CanvasPropertiesState,
    drawnObjects: CanvasDrawnObjects,
    onAddItem: (CanvasItemModel) -> Unit,
    onSelectItem: (UUID) -> Unit,
    onPanSelectedItem: (item: UUID, pan: Offset) -> Unit,
    onResizeSelectedItem: (item: UUID, rect: Rect) -> Unit,
    onRotateSelectedItem: (item: UUID, degree: Float) -> Unit,
    onZoomCanvas: (Float) -> Unit,
    onPanCanvas: (Offset) -> Unit,
    modifier: Modifier = Modifier,
    onDeSelectItem: () -> Unit = {},
) {

    val hasAction by remember(state.action) {
        derivedStateOf { state.action != null }
    }

    Box(
        modifier = modifier
            .thenIf(
                condition = hasAction,
                modifier = Modifier.pointerHoverIcon(PointerIcon.Crosshair)
            )
            .doubleScrollOrZoom(onPan = onPanCanvas, onZoom = onZoomCanvas)
            .observeItemInteractions(
                enabled = state.isSelectAction,
                items = drawnObjects,
                properties = properties,
                onSelectObject = onSelectItem,
                onPanCanvasItem = onPanSelectedItem,
                onDeselectObject = onDeSelectItem,
                onResizeCanvasItem = onResizeSelectedItem,
                onRotateItem = onRotateSelectedItem
            )
    ) {
        Spacer(
            modifier = Modifier.matchParentSize()
                .drawGraphLines(showGraph = properties.showGraphLines)
                .drawCanvasItems(
                    state = state,
                    properties = properties,
                    drawnObjects = drawnObjects
                )
                .onDrawViaActionBarAction(
                    actionBarState = state,
                    style = style,
                    properties = properties,
                    onAddItem = onAddItem
                )
        )
    }
}

@Composable
fun DrawingCanvas(
    state: ActionBarState,
    style: CanvasDrawStyle,
    propertiesState: CanvasPropertiesState,
    drawnObjects: CanvasDrawnObjects,
    onCanvasPropertiesEvent: (CanvasPropertiesEvent) -> Unit,
    onInteractionEvent: (CanvasItemEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    DrawingCanvas(
        state = state,
        style = style,
        properties = propertiesState,
        drawnObjects = drawnObjects,
        onAddItem = { onInteractionEvent(CanvasItemEvent.OnAddNewCanvasItem(it)) },
        onSelectItem = { onInteractionEvent(CanvasItemEvent.OnSelectCanvasItem(it)) },
        onDeSelectItem = { onInteractionEvent(CanvasItemEvent.OnDeSelectCanvasItem) },
        onZoomCanvas = { onCanvasPropertiesEvent(CanvasPropertiesEvent.OnZoom(it)) },
        onPanCanvas = { onCanvasPropertiesEvent(CanvasPropertiesEvent.OnPanCanvas(it)) },
        onPanSelectedItem = { item, pan -> onInteractionEvent(CanvasItemEvent.OnMoveSelectedItem(item, pan)) },
        onResizeSelectedItem = { item, rect -> onInteractionEvent(CanvasItemEvent.OnResizeSelectedItem(item, rect)) },
        onRotateSelectedItem = { item, degree ->
            onInteractionEvent(CanvasItemEvent.OnRotateSelectedItem(item, degree))
        },
        modifier = modifier,
    )
}


@Preview
@Composable
fun DrawingCanvasPreview() = DrawItAppTheme {
    DrawingCanvas(
        state = ActionBarState(),
        style = CanvasDrawStyle(),
        propertiesState = CanvasPropertiesState(),
        drawnObjects = CanvasDrawnObjects(),
        onInteractionEvent = {},
        onCanvasPropertiesEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}