package presentation.drawing.utils

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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import models.CanvasDrawnObjects
import models.CanvasItemModel.Companion.AXLE_POSITION_OFFSET
import models.CanvasPropertiesState
import utils.thenIf
import java.util.*
import kotlin.math.abs
import kotlin.math.sign

private val POINTER_RANGE = -5f..5f

@OptIn(ExperimentalComposeUiApi::class)
internal fun Modifier.observeItemInteractions(
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
            if (isResizing) return@rememberTransformableState
            items.selectedUUID?.let { item -> onPanCanvasItem(item, panChange) }
        },
    )

    val isSelectedAndNotOnBoundary by remember(items.selectedUUID, itemBoundary) {
        derivedStateOf { items.selectedUUID != null && !itemBoundary.isOnBoundary && !itemBoundary.isRotateAxle }
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
                newPosition = change.position,
                properties = properties,
                pivot = componentCenter
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
                itemBoundary = itemBoundary.copy(
                    onNEBoundary = kindOfTopRightCorner(change.position),
                    onNWBoundary = kindOfTopLeftCorner(change.position),
                    onSWBoundary = kindOfBottomLeftCorner(change.position),
                    onSEBoundary = kindOfBottomRightCorner(change.position),
                    onTBoundary = onTopBoundary(change.position),
                    onBBoundary = onBottomBoundary(change.position),
                    onLBoundary = onLeftBoundary(change.position),
                    onRBoundary = onRightBoundary(change.position),
                    isRotateAxle = onRotationAxlePosition(change.position, this@onPointerEvent)
                )
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
        .selectHoverIcon(boundary = itemBoundary)
        .onSizeChanged { size -> componentCenter = size.center.toOffset() }
}

@OptIn(ExperimentalComposeUiApi::class)
internal fun Modifier.doubleScrollOrZoom(
    onPan: (Offset) -> Unit,
    onZoom: (Float) -> Unit,
    speed: Float = 10f,
) = then(
    Modifier.onPointerEvent(eventType = PointerEventType.Scroll) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent
        val scrollDelta = change.scrollDelta * -1f
        // FIXME: Fix the problems regrading proper scroll

        // zoom events are made when a scroll delta y component is 1 or -1
        if (abs(scrollDelta.y) == 1f) {
            val zoomSign = scrollDelta.y.sign
            onZoom(zoomSign)
        }
        onPan(scrollDelta.times(speed))
    }
)

private fun Modifier.selectHoverIcon(
    boundary: CanvasItemPointerPosition,
) = composed {

    val hasIcon by remember(boundary) { derivedStateOf { boundary.cursorIcon != null } }
    val cursorIcon = remember(boundary) { boundary.cursorIcon }

    thenIf(
        condition = hasIcon,
        modifier = cursorIcon?.let { cursor -> pointerHoverIcon(cursor) }
    )
}

private fun Rect.insideInteractionBoundary(
    offset: Offset,
    inflateDelta: Float = POINTER_RANGE.endInclusive,
    deflateDelta: Float = POINTER_RANGE.endInclusive,
) = inflate(inflateDelta).contains(offset) && !deflate(deflateDelta).contains(offset)

private fun Rect.onTopBoundary(offset: Offset) = offset.y - top in POINTER_RANGE &&
        offset.x > left && offset.x < right

private fun Rect.onBottomBoundary(offset: Offset) = offset.y - bottom in POINTER_RANGE &&
        offset.x > left && offset.x < right

private fun Rect.onLeftBoundary(offset: Offset) = offset.x - left in POINTER_RANGE &&
        offset.y > top && offset.y < bottom

private fun Rect.onRightBoundary(offset: Offset) = offset.x - right in POINTER_RANGE &&
        offset.y > top && offset.y < bottom

private fun Rect.onRotationAxlePosition(offset: Offset, density: Density) = with(density) {
    with(density) {
        val axleOffPosition = topCenter - Offset(0f, AXLE_POSITION_OFFSET)
        offset kindOfEqualTo axleOffPosition
    }
}


private fun Rect.kindOfTopLeftCorner(offset: Offset) = offset kindOfEqualTo topLeft
private fun Rect.kindOfBottomRightCorner(offset: Offset) = offset kindOfEqualTo bottomRight
private fun Rect.kindOfTopRightCorner(offset: Offset) = offset kindOfEqualTo topRight
private fun Rect.kindOfBottomLeftCorner(offset: Offset) = offset kindOfEqualTo bottomLeft

private infix fun Offset.kindOfEqualTo(other: Offset): Boolean = (this - other).let { diff ->
    diff.x in POINTER_RANGE && diff.y in POINTER_RANGE
}
