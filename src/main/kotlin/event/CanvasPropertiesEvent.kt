package event

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

sealed interface CanvasPropertiesEvent {
    data object OnUndo : CanvasPropertiesEvent
    data object OnRedo : CanvasPropertiesEvent
    data object OnIncrementZoom : CanvasPropertiesEvent
    data object OnDecrementZoom : CanvasPropertiesEvent
    data object OnResetZoom : CanvasPropertiesEvent
    data class OnZoom(val amount: Float) : CanvasPropertiesEvent
    data class OnPanCanvas(val canvasSize: IntSize, val amount: Offset) : CanvasPropertiesEvent
}