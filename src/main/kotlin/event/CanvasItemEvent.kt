package event

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import models.CanvasItemModel
import java.util.*

sealed interface CanvasItemEvent {
    data class OnAddNewCanvasItem(val item: CanvasItemModel) : CanvasItemEvent
    data class OnSelectCanvasItem(val item: CanvasItemModel) : CanvasItemEvent
    data object OnDeSelectCanvasItem : CanvasItemEvent
    data class OnMoveSelectedItem(val itemUUID: UUID, val panOffset: Offset) : CanvasItemEvent
    data class OnResizeSelectedItem(val itemUUID: UUID, val newRect: Rect) : CanvasItemEvent
}