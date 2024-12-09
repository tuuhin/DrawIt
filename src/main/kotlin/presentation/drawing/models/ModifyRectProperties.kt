package presentation.drawing.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import models.CanvasItemModel
import models.CanvasPropertiesState

fun CanvasItemModel.modifiedRectFromProperties(properties: CanvasPropertiesState, pivot: Offset)
        : CenterPivotRotatedRect {
    val calScale = properties.canvasScale / scale
    return with(boundingRect) {
        // Calculate distances from pivot to each edge
        val distanceLeft = left - pivot.x
        val distanceTop = top - pivot.y
        val distanceRight = right - pivot.x
        val distanceBottom = bottom - pivot.y

        // Apply scaling to each distance
        val scaledLeft = pivot.x + distanceLeft * calScale
        val scaledTop = pivot.y + distanceTop * calScale
        val scaledRight = pivot.x + distanceRight * calScale
        val scaledBottom = pivot.y + distanceBottom * calScale

        // Return the new scaled rectangle
        val rect = Rect(left = scaledLeft, top = scaledTop, right = scaledRight, bottom = scaledBottom)
            .translate(properties.panedScaledOffset)

        CenterPivotRotatedRect.fromRectAndAngle(rect, rotateInRadians)
    }
}
