package models

import androidx.compose.runtime.Stable

@Stable
data class CanvasDrawnObjects(
    val objects: List<CanvasItemModel> = emptyList(),
    val selectedObject: CanvasItemModel? = null,
)