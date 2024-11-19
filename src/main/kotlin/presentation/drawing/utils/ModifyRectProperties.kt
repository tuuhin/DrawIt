package presentation.drawing.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import models.CanvasItemModel
import models.CanvasPropertiesState
import kotlin.math.cos
import kotlin.math.sin

fun CanvasItemModel.modifiedRectFromProperties(properties: CanvasPropertiesState, pivot: Offset): Rect {
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
        Rect(left = scaledLeft, top = scaledTop, right = scaledRight, bottom = scaledBottom)
            .translate(properties.panedScaledOffset)
            .rotate(rotateInRadians)
    }
}

fun rotatePoint(x: Float, y: Float, pivot: Offset, radians: Float): Offset {
    val dx = x - pivot.x
    val dy = y - pivot.y
    val rotatedX = cos(radians) * dx - sin(radians) * dy + pivot.x
    val rotatedY = sin(radians) * dx + cos(radians) * dy + pivot.y
    return Offset(rotatedX, rotatedY)
}

fun reverseRotatePoint(x: Float, y: Float, pivot: Offset, radians: Float): Offset {
    val dx = x - pivot.x
    val dy = y - pivot.y
    val rotatedX = cos(-radians) * dx - sin(-radians) * dy + pivot.x
    val rotatedY = sin(-radians) * dx + cos(-radians) * dy + pivot.y
    return Offset(rotatedX, rotatedY)
}


private fun Rect.rotate(radians: Float): Rect {
    val corners = listOf(
        rotatePoint(left, top, center, radians),
        rotatePoint(right, top, center, radians),
        rotatePoint(left, bottom, center, radians),
        rotatePoint(right, bottom, center, radians)
    )
    val newLeft = corners.minOf { it.x }
    val newTop = corners.minOf { it.y }
    val newRight = corners.maxOf { it.x }
    val newBottom = corners.maxOf { it.y }

    // Return the new rectangle
    return Rect(newLeft, newTop, newRight, newBottom)
}