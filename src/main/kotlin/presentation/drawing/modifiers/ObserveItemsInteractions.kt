package presentation.drawing.modifiers

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import models.CanvasDrawnObjects
import models.CanvasItemModel
import models.CanvasPropertiesState
import presentation.drawing.models.CanvasItemPointerPosition
import presentation.drawing.models.modifiedRectFromProperties
import utils.thenIf
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.observeItemInteractions(
    items: CanvasDrawnObjects,
    properties: CanvasPropertiesState,
    onSelectObject: (UUID) -> Unit,
    onPanCanvasItem: (item: UUID, pan: Offset) -> Unit,
    onResizeCanvasItem: (item: UUID, newSize: Rect) -> Unit,
    onRotateItem: (item: UUID, degree: Float) -> Unit,
    onDeselectObject: () -> Unit = {},
    enabled: Boolean = true,
) = composed {
    if (!enabled) return@composed Modifier

    // We need to use the center as the pivot for scaling purpose as all the components
    // are scaled w.r.t to the center of the canvas
    var componentCenter by remember { mutableStateOf(Offset.Zero) }

    var itemBoundary by remember { mutableStateOf(CanvasItemPointerPosition()) }

    // flag to indicate if the canvas item is being resized as we cannot move the item on resize
    var isResizing by remember { mutableStateOf(false) }
    // flag to indicate if the canvas item is being rotated as again move is not allowed during rotating
    var isRotate by remember { mutableStateOf(false) }

    val transformableState = rememberTransformableState(
        onTransformation = { _, panChange, _ ->
            if (isResizing || isRotate) return@rememberTransformableState
            items.selectedUUID?.let { item -> onPanCanvasItem(item, panChange) }
        },
    )

    val isSelectedAndNotOnBoundary by remember(items.selectedUUID, itemBoundary) {
        derivedStateOf { items.selectedUUID != null && itemBoundary.isNotOrBoundaryOrAxle }
    }

    val scaledCanvasItemsBoundaryRect by remember(items.canvasItems, properties, componentCenter) {
        derivedStateOf {
            items.canvasItems.map { item ->
                item.uuid to item.modifiedRectFromProperties(properties, componentCenter)
            }
        }
    }

    onPointerEvent(eventType = PointerEventType.Move) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent

        // check if the point is inside item
        val isInside = scaledCanvasItemsBoundaryRect.any { (_, item) -> item.contains(change.position) }
        // set inside if the item is inside and not resizing
        itemBoundary = itemBoundary.copy(isInside = isInside && !isResizing && !isRotate)
        // get the selected item if present
        val selectedItem = items.selectedItem ?: return@onPointerEvent

        if (isResizing) {
            // resize it as per the conditions
            val newRect = itemBoundary.toNewBounds(
                item = selectedItem,
                pointerPosition = change.position,
                properties = properties,
                canvasCenter = componentCenter
            )
            onResizeCanvasItem(selectedItem.uuid, newRect)
        } else if (isRotate) {
            val angle = itemBoundary.toNewRotateAngle(selectedItem, change.position)
            onRotateItem(selectedItem.uuid, angle)
        } else {
            val scaledItem = selectedItem.modifiedRectFromProperties(properties = properties, pivot = componentCenter)
            with(scaledItem) {
                // if the hover points not in interaction boundary, no need to consider them
                if (!insideInteractionBoundary(change.position, inflateDelta = 20.dp.toPx())) {
                    // reset the boundary params
                    itemBoundary = itemBoundary.resetBoundaryPoints()
                    return@onPointerEvent
                }
                // check for points for boundary extensions
                itemBoundary = scaledItem.updatePointerPosition(change.position, this@onPointerEvent)
            }
        }
    }.onPointerEvent(eventType = PointerEventType.Press, pass = PointerEventPass.Final) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent
        // if the item is on the boundary, then its set resize to true
        if (itemBoundary.isOnBoundary) isResizing = true
        else if (itemBoundary.isRotateAxle) isRotate = true
        // select the item if it matches
        scaledCanvasItemsBoundaryRect.findLast { (_, rect) -> rect.contains(change.position) }
            ?.let { (uuid, _) -> onSelectObject(uuid) }
            ?: kotlin.run {
                if (isResizing) return@run
                if (isRotate) return@run
                onDeselectObject()
                // on deselected resizing is automatically false
                isResizing = false
                isRotate = false
            }
    }.onPointerEvent(eventType = PointerEventType.Release, pass = PointerEventPass.Final) { event ->
        if (event.changes.isEmpty()) return@onPointerEvent
        // on release is resizing is false and the initial point is also zero
        if (isResizing) isResizing = false
        if (isRotate) isRotate = false
    }
        .transformable(state = transformableState, enabled = isSelectedAndNotOnBoundary)
        .selectHoverIcon(boundary = itemBoundary, selectedItem = items.selectedItem)
        .onSizeChanged { size -> componentCenter = size.center.toOffset() }
}

private fun Modifier.selectHoverIcon(
    selectedItem: CanvasItemModel?,
    boundary: CanvasItemPointerPosition,
) = composed {

    val cursorIcon = remember(boundary) {
        boundary.pickCursorIcon(selectedItem?.rotateInRadians ?: 0.0f)
    }

    thenIf(
        condition = selectedItem != null,
        modifier = cursorIcon?.let { cursor -> pointerHoverIcon(cursor) }
    )
}
