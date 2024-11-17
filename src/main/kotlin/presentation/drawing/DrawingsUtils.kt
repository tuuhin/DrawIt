package presentation.drawing

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import models.CanvasDrawnObjects
import models.CanvasItemModel
import models.CanvasPropertiesState
import org.jetbrains.skiko.Cursor
import utils.thenIf
import java.util.*
import kotlin.math.abs
import kotlin.math.sign

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

    var componentCenter by remember { mutableStateOf(Offset.Zero) }
    var itemBoundary by remember { mutableStateOf(CanvasItemBoundary()) }
    var isResizing by remember { mutableStateOf(false) }

    val transformableState = rememberTransformableState(
        onTransformation = { _, panChange, _ ->
            if (isResizing) return@rememberTransformableState
            items.selectedUUID?.let { item -> onPanCanvasItem(item, panChange) }
        },
    )

    val isSelectedAndNotOnBoundary by remember(items.selectedUUID, itemBoundary.isOnBoundary) {
        derivedStateOf { items.selectedUUID != null && !itemBoundary.isOnBoundary }
    }

    val scaledCanvasItemsBoundaryRect by remember(items.canvasItems, properties, componentCenter) {
        derivedStateOf {
            items.canvasItems.map { item ->
                item.uuid to item.modifyRectViaProperties(properties, componentCenter)
            }
        }
    }

    onPointerEvent(eventType = PointerEventType.Move) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent

        // check if the point is inside item
        val isInside = scaledCanvasItemsBoundaryRect.any { (_, item) -> item.contains(change.position) }
        // set inside if the item is inside and not resizing
        itemBoundary = itemBoundary.copy(isInside = isInside && !isResizing)
        // get the selected item if present
        val selectedItem = items.selectedItem ?: return@onPointerEvent

        if (isResizing) {
            // resize it as per the conditions
            val newRect = itemBoundary.toNewBounds(
                rect = selectedItem.boundingRect,
                newPosition = change.position,
                properties = properties
            )
            onResizeCanvasItem(selectedItem.uuid, newRect)
        } else {
            val scaledItem = selectedItem.modifyRectViaProperties(properties = properties, pivot = componentCenter)
            with(scaledItem) {
                // if the hover points not in interaction boundary, no need to consider them
                if (!insideInteractionBoundary(change.position)) {
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
                    onRBoundary = onRightBoundary(change.position)
                )
            }
        }
    }.onPointerEvent(eventType = PointerEventType.Press, pass = PointerEventPass.Final) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent
        // if the item is on the boundary, then its set resize to true
        if (itemBoundary.isOnBoundary) isResizing = true
        // select the item if it matches
        scaledCanvasItemsBoundaryRect.findLast { (_, rect) -> rect.contains(change.position) }
            ?.let { (uuid, _) -> onSelectObject(uuid) }
            ?: kotlin.run {
                if (isResizing) return@run
                onDeselectObject()
                // on deselected resizing is automatically false
                isResizing = false
            }
    }.onPointerEvent(eventType = PointerEventType.Release, pass = PointerEventPass.Final) { event ->
        if (!isResizing && event.changes.isEmpty()) return@onPointerEvent
        // on release is resizing is false and the initial point is also zero
        isResizing = false
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
    boundary: CanvasItemBoundary,
) = composed {

    val hasIcon by remember(boundary) { derivedStateOf { boundary.cursorIcon != null } }
    val cursorIcon = remember(boundary) { boundary.cursorIcon }

    thenIf(
        condition = hasIcon,
        modifier = cursorIcon?.let { cursor -> pointerHoverIcon(cursor) }
    )
}

private fun Rect.insideInteractionBoundary(offset: Offset, delta: Float = 5f) =
    inflate(delta).contains(offset) && !deflate(delta).contains(offset)

private fun Rect.onTopBoundary(offset: Offset) = offset.y - top in (-5f..5f) &&
        offset.x > left && offset.x < right

private fun Rect.onBottomBoundary(offset: Offset) = offset.y - bottom in (-5f..5f) &&
        offset.x > left && offset.x < right

private fun Rect.onLeftBoundary(offset: Offset) = offset.x - left in (-5f..5f) &&
        offset.y > top && offset.y < bottom

private fun Rect.onRightBoundary(offset: Offset) = offset.x - right in (-5f..5f) &&
        offset.y > top && offset.y < bottom

private fun Rect.kindOfTopLeftCorner(offset: Offset) = offset kindOfEqualTo topLeft
private fun Rect.kindOfBottomRightCorner(offset: Offset) = offset kindOfEqualTo bottomRight
private fun Rect.kindOfTopRightCorner(offset: Offset) = offset kindOfEqualTo topRight
private fun Rect.kindOfBottomLeftCorner(offset: Offset) = offset kindOfEqualTo bottomLeft

private infix fun Offset.kindOfEqualTo(other: Offset): Boolean = (this - other).let { diff ->
    diff.x in (-5f..5f) && diff.y in (-5f..5f)
}

private fun CanvasItemModel.modifyRectViaProperties(properties: CanvasPropertiesState, pivot: Offset): Rect {
    val calScale = properties.scale / scale
    return with(boundingRect) {
        // Calculate distances from pivot to each edge
        val distanceLeft = left - pivot.x
        val distanceTop = top - pivot.y
        val distanceRight = right - pivot.x
        val distanceBottom = bottom - pivot.y

        // Apply scaling to each distance
        val scaledLeft = pivot.x + distanceLeft * calScale
        val scaledTop = pivot.y + distanceTop * calScale
        val scaledRight = pivot.x + distanceRight * calScale
        val scaledBottom = pivot.y + distanceBottom * calScale

        // Return the new scaled rectangle
        Rect(left = scaledLeft, top = scaledTop, right = scaledRight, bottom = scaledBottom)
            .translate(properties.pannedScaledOffset)
    }
}

private data class CanvasItemBoundary(
    val onNEBoundary: Boolean = false,
    val onNWBoundary: Boolean = false,
    val onSEBoundary: Boolean = false,
    val onSWBoundary: Boolean = false,
    val onTBoundary: Boolean = false,
    val onBBoundary: Boolean = false,
    val onLBoundary: Boolean = false,
    val onRBoundary: Boolean = false,
    val isInside: Boolean = false,
) {
    private val isVertical: Boolean
        get() = onTBoundary || onBBoundary

    private val isHorizontal: Boolean
        get() = onLBoundary || onRBoundary

    val isCorner: Boolean
        get() = onNEBoundary || onNWBoundary || onSWBoundary || onSEBoundary

    val isOnBoundary: Boolean
        get() = isCorner || isVertical || isHorizontal

    val cursorIcon: PointerIcon?
        get() {
            val cursor = when {
                isInside -> Cursor.MOVE_CURSOR
                onNEBoundary -> Cursor.NE_RESIZE_CURSOR
                onNWBoundary -> Cursor.NW_RESIZE_CURSOR
                onSEBoundary -> Cursor.SE_RESIZE_CURSOR
                onSWBoundary -> Cursor.SW_RESIZE_CURSOR
                onTBoundary -> Cursor.N_RESIZE_CURSOR
                onBBoundary -> Cursor.S_RESIZE_CURSOR
                onLBoundary -> Cursor.W_RESIZE_CURSOR
                onRBoundary -> Cursor.E_RESIZE_CURSOR
                else -> null
            }
            return cursor?.let { PointerIcon(Cursor.getPredefinedCursor(it)) }
        }

    fun resetBoundaryPoints() = CanvasItemBoundary(isInside = isInside)

    fun toNewBounds(rect: Rect, newPosition: Offset, properties: CanvasPropertiesState): Rect {
        val position = newPosition - properties.pannedScaledOffset
        return with(rect) {
            when {
                onNEBoundary -> Rect(left, position.y, position.x, bottom)
                onNWBoundary -> Rect(position.x, position.y, right, bottom)
                onSEBoundary -> Rect(left, top, position.x, position.y)
                onSWBoundary -> Rect(position.x, top, right, position.y)
                else -> Rect(
                    left = if (onLBoundary) position.x else left,
                    top = if (onTBoundary) position.y else top,
                    right = if (onRBoundary) position.x else right,
                    bottom = if (onBBoundary) position.y else bottom
                )
            }
        }
    }
}
