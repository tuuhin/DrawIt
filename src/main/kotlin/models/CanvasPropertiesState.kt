package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset

data class CanvasPropertiesState(
    val canvasScale: Float = 1f,
    val panedCanvas: Offset = Offset.Zero,
    val undoEnabled: Boolean = false,
    val redoEnabled: Boolean = false,
    val showGraphLines: Boolean = false,
) {

    val panedScaledOffset: Offset
        get() = (panedCanvas * canvasScale).round().toOffset()
}
