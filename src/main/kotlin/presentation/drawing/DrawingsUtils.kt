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
import models.CanvasDrawnObjects
import models.CanvasItemModel
import org.jetbrains.skiko.Cursor
import utils.thenIf
import java.util.*
import kotlin.math.abs
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class)
internal fun Modifier.observeItemInteractions(
    items: CanvasDrawnObjects,
    onSelectObject: (CanvasItemModel) -> Unit,
    onPanCanvasItem: (item: UUID, pan: Offset) -> Unit,
    onResizeCanvasItem: (item: UUID, newSize: Rect) -> Unit,
    onDeselectObject: () -> Unit = {},
    enabled: Boolean = true,
) = composed {
    if (!enabled) return@composed Modifier

    val transformableState = rememberTransformableState(
        onTransformation = { _, panChange, _ ->
            items.selectedUUID?.let { item -> onPanCanvasItem(item, panChange) }
        },
    )

    val isSelected by remember(items.selectedUUID) {
        derivedStateOf { items.selectedUUID != null }
    }

    var itemBoundary by remember { mutableStateOf(CanvasItemBoundary()) }
    var movement by remember { mutableStateOf(Offset.Zero) }

    onPointerEvent(eventType = PointerEventType.Move) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent

        // check if the point is inside item
        val isInside = items.canvasItems.any { item -> item.boundingRect.contains(change.position) }
        itemBoundary = itemBoundary.copy(isInside = isInside)

        // if there's a selected item and the pointer is moved, then change the icons accordingly
        items.selectedItem?.let { selectedItem ->
            with(selectedItem) {
                // if the hover points not in interaction boundary, no need to consider them
                if (!boundingRect.insideInteractionBoundary(change.position)) {
                    // reset the boundary params
                    itemBoundary = itemBoundary.resetBoundaryPoints()
                    return@onPointerEvent
                }

                // check for points for boundary extensions
                itemBoundary = itemBoundary.copy(
                    onNEBoundary = boundingRect.kindOfTopRightCorner(change.position),
                    onNWBoundary = boundingRect.kindOfTopLeftCorner(change.position),
                    onSWBoundary = boundingRect.kindOfBottomLeftCorner(change.position),
                    onSEBoundary = boundingRect.kindOfBottomRightCorner(change.position),
                    onTBoundary = boundingRect.onTopBoundary(change.position),
                    onBBoundary = boundingRect.onBottomBoundary(change.position),
                    onLBoundary = boundingRect.onLeftBoundary(change.position),
                    onRBoundary = boundingRect.onRightBoundary(change.position)
                )
            }
        }
    }.onPointerEvent(eventType = PointerEventType.Press, pass = PointerEventPass.Final) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent
        // matching object
        items.canvasItems.findLast { it.boundingRect.contains(change.position) }
            ?.let { item -> onSelectObject(item) }
            ?: onDeselectObject()

    }
        .transformable(state = transformableState, enabled = isSelected)
        .selectHoverIcon(boundary = itemBoundary)

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

    private val isCorner: Boolean
        get() = onNEBoundary || onNWBoundary || onSWBoundary || onSEBoundary

    private val isVertical: Boolean
        get() = onTBoundary || onBBoundary

    private val isHorizontal: Boolean
        get() = onLBoundary || onRBoundary

    val isInsideOrOnBoundary: Boolean
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
}