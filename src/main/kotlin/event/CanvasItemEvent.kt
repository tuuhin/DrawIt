package event

import androidx.compose.ui.geometry.Offset
import models.CanvasItemModel

sealed interface CanvasItemEvent {
    data class OnAddNewCanvasItem(val item: CanvasItemModel) : CanvasItemEvent
    data class OnSelectCanvasItem(val item: CanvasItemModel) : CanvasItemEvent
    data object OnDeSelectCanvasItem : CanvasItemEvent
    data class OnMoveSelectedItem(val item: CanvasItemModel, val panOffset: Offset) : CanvasItemEvent
}