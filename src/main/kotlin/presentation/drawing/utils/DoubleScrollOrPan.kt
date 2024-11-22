package presentation.drawing.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlin.math.abs
import kotlin.math.sign

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