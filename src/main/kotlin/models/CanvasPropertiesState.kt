package models

import androidx.compose.ui.geometry.Offset

data class CanvasPropertiesState(
    val scale: Float = 1f,
    val panedCanvas: Offset = Offset.Zero,
    val undoEnabled: Boolean = false,
    val redoEnabled: Boolean = false,
)
