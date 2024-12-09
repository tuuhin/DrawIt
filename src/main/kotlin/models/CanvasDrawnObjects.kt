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

    val itemsUUIDS: List<UUID>
        get() = canvasItems.map(CanvasItemModel::uuid)


    fun updateMatchingItem(
        predicate: (CanvasItemModel) -> Boolean,
        update: (CanvasItemModel) -> CanvasItemModel,
    ): List<CanvasItemModel> = canvasItems.map { item -> if (predicate(item)) update(item) else item }

}