package models

import androidx.compose.runtime.Stable
import java.util.*

@Stable
data class CanvasDrawnObjects(
    val canvasItems: List<CanvasItemModel> = emptyList(),
    val selectedUUID: UUID? = null,
) {
    val selectedItem: CanvasItemModel?
        get() = canvasItems.find { it.uuid == selectedUUID }
}