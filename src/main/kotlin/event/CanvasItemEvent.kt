package event

import models.CanvasItemModel

sealed interface CanvasItemEvent {
    data class OnAddNewCanvasItem(val item: CanvasItemModel) : CanvasItemEvent
    data class OnSelectCanvasItem(val item: CanvasItemModel) : CanvasItemEvent
    data object OnDeSelectCanvasItem : CanvasItemEvent
}