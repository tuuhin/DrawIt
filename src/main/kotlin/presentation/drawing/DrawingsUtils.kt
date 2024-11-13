package presentation.drawing

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import models.CanvasDrawnObjects
import models.CanvasItemModel
import org.jetbrains.skiko.Cursor
import utils.thenIf
import kotlin.math.abs
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class)
internal fun Modifier.observeMouseMovements(
    items: CanvasDrawnObjects,
    onSelectObject: (CanvasItemModel) -> Unit,
    onPanCanvasItem: (item: CanvasItemModel, pan: Offset) -> Unit,
    onDeselectObject: () -> Unit = {},
    enabled: Boolean = true,
) = composed {
    if (!enabled) return@composed Modifier

    val transformableState = rememberTransformableState(
        onTransformation = { _, panChange, _ ->
            items.selectedObject?.let { item ->
                onPanCanvasItem(
                    item,
                    panChange
                )
            }
        },
    )

    val isSelected by remember(items.selectedObject) {
        derivedStateOf { items.selectedObject != null }
    }

    onPointerEvent(eventType = PointerEventType.Press, pass = PointerEventPass.Final) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent
        // matching object
        items.objects.findLast { it.boundingRect.contains(change.position) }
            ?.let { item -> onSelectObject(item) }
            ?: onDeselectObject()
    }
        .transformable(state = transformableState, enabled = isSelected)
        .thenIf(
            condition = isSelected,
            modifier = pointerHoverIcon(icon = PointerIcon(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)))
        )
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

        // zoom events are made when scroll delta y component is 1 or -1
        if (abs(scrollDelta.y) == 1f) {
            val zoomSign = scrollDelta.y.sign
            onZoom(zoomSign)
        }
        onPan(scrollDelta.times(speed))
    }
)