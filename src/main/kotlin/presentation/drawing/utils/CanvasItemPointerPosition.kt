package presentation.drawing.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerIcon
import models.CanvasItemModel
import models.CanvasPropertiesState
import org.jetbrains.skiko.Cursor
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

data class CanvasItemPointerPosition(
    val onNEBoundary: Boolean = false,
    val onNWBoundary: Boolean = false,
    val onSEBoundary: Boolean = false,
    val onSWBoundary: Boolean = false,
    val onTBoundary: Boolean = false,
    val onBBoundary: Boolean = false,
    val onLBoundary: Boolean = false,
    val onRBoundary: Boolean = false,
    val isInside: Boolean = false,
    val isRotateAxle: Boolean = false,
) {
    private val isVertical: Boolean
        get() = onTBoundary || onBBoundary

    private val isHorizontal: Boolean
        get() = onLBoundary || onRBoundary

    private val isCorner: Boolean
        get() = onNEBoundary || onNWBoundary || onSWBoundary || onSEBoundary

    val isOnBoundary: Boolean
        get() = isCorner || isVertical || isHorizontal

    val cursorIcon: PointerIcon?
        get() {
            val cursor = when {
                isRotateAxle -> Cursor.HAND_CURSOR
                isInside -> Cursor.MOVE_CURSOR
                onNEBoundary -> Cursor.NE_RESIZE_CURSOR
                onNWBoundary -> Cursor.NW_RESIZE_CURSOR
                onSEBoundary -> Cursor.SE_RESIZE_CURSOR
                onSWBoundary -> Cursor.SW_RESIZE_CURSOR
                onTBoundary -> Cursor.N_RESIZE_CURSOR
                onBBoundary -> Cursor.S_RESIZE_CURSOR
                onLBoundary -> Cursor.W_RESIZE_CURSOR
                onRBoundary -> Cursor.E_RESIZE_CURSOR
                else -> null
            }
            return cursor?.let { PointerIcon(Cursor.getPredefinedCursor(it)) }
        }

    fun resetBoundaryPoints() = CanvasItemPointerPosition(isInside = isInside)

    fun toNewBounds(
        item: CanvasItemModel,
        newPosition: Offset,
        properties: CanvasPropertiesState,
        pivot: Offset,
    ): Rect {
        val scaledPosition = ((newPosition - pivot) * item.scale) / properties.canvasScale
        val position = scaledPosition + pivot - properties.panedCanvas
        return with(item.boundingRect) {
            when {
                onNEBoundary -> Rect(left, position.y, position.x, bottom)
                onNWBoundary -> Rect(position.x, position.y, right, bottom)
                onSEBoundary -> Rect(left, top, position.x, position.y)
                onSWBoundary -> Rect(position.x, top, right, position.y)
                else -> Rect(
                    left = if (onLBoundary) position.x else left,
                    top = if (onTBoundary) position.y else top,
                    right = if (onRBoundary) position.x else right,
                    bottom = if (onBBoundary) position.y else bottom
                )
            }
        }
    }

    fun toNewRotateAngle(item: CanvasItemModel, newPosition: Offset): Float {
        with(item.boundingRect) {
            val otherPoint = Offset(center.x, newPosition.y)
            val distanceAxleToNewPos = Offset(otherPoint.x - center.x, otherPoint.y - center.y)
            val distanceCenterToNewPos = Offset(newPosition.x - center.x, newPosition.y - center.y)

            val dotProduct = distanceAxleToNewPos.x * distanceCenterToNewPos.x +
                    distanceAxleToNewPos.y * distanceCenterToNewPos.y

            val magnitudeAB = sqrt(distanceAxleToNewPos.x.pow(2) + distanceAxleToNewPos.y.pow(2))
            val magnitudeBC = sqrt(distanceCenterToNewPos.x.pow(2) + distanceCenterToNewPos.y.pow(2))

            // Avoid division by zero
            if (magnitudeAB == 0.0f || magnitudeBC == 0.0f) return 0.0f

            val cosTheta = (dotProduct / (magnitudeAB * magnitudeBC)).toDouble()

            val angle = Math.toDegrees(acos(cosTheta.coerceIn(-1.0, 1.0))).toFloat()
            println(angle)
            return angle
        }
    }
}
