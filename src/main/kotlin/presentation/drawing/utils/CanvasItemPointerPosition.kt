package presentation.drawing.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerIcon
import models.CanvasItemModel
import models.CanvasPropertiesState
import org.jetbrains.skiko.Cursor
import kotlin.math.PI
import kotlin.math.acos

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

    val isNotOrBoundaryOrAxle: Boolean
        get() = !isOnBoundary || !isRotateAxle

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

    fun toNewBounds(item: CanvasItemModel, position: Offset, properties: CanvasPropertiesState, pivot: Offset): Rect {

        val scaledPosition = (position - pivot) * (item.scale / properties.canvasScale)
        val finalPosition = scaledPosition + pivot - properties.panedCanvas

        return with(item.boundingRect) {
            when {
                onNEBoundary -> Rect(left, finalPosition.y, finalPosition.x, bottom)
                onNWBoundary -> Rect(finalPosition.x, finalPosition.y, right, bottom)
                onSEBoundary -> Rect(left, top, finalPosition.x, finalPosition.y)
                onSWBoundary -> Rect(finalPosition.x, top, right, finalPosition.y)
                else -> Rect(
                    left = if (onLBoundary) finalPosition.x else left,
                    top = if (onTBoundary) finalPosition.y else top,
                    right = if (onRBoundary) finalPosition.x else right,
                    bottom = if (onBBoundary) finalPosition.y else bottom
                )
            }
        }

    }

    fun toNewRotateAngle(item: CanvasItemModel, newPosition: Offset): Float {

        val centerToTopCenter = with(item.boundingRect) { topCenter - center }
        val centerToNewPos = with(item.boundingRect) { newPosition - center }

        val dotProduct = centerToTopCenter.x * centerToNewPos.x + centerToTopCenter.y * centerToNewPos.y

        val magnitudeAB = centerToTopCenter.getDistance()
        val magnitudeBC = centerToNewPos.getDistance()

        val distanceProduct = magnitudeAB * magnitudeBC

        // Avoid division by zero
        if (distanceProduct == 0.0f) return 0.0f

        val cosTheta = (dotProduct / distanceProduct).coerceIn(-1.0f..1.0f)
        val angleInRadians = acos(cosTheta)

        // adjust the angles
        val isOtherHalf = item.boundingRect.center.x - newPosition.x < 0
        return if (isOtherHalf) angleInRadians
        else (2 * PI - angleInRadians).toFloat()
    }
}
