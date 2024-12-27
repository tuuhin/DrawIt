package presentation.drawing.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerIcon
import models.CanvasItemModel
import models.CanvasPropertiesState
import org.jetbrains.skiko.Cursor
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.roundToInt

data class CanvasItemPointerPosition(
    val corner: CornerSide = CornerSide.NONE,
    val isInside: Boolean = false,
    val isRotateAxle: Boolean = false,
) {

    val isOnBoundary: Boolean
        get() = corner != CornerSide.NONE

    val isNotOrBoundaryOrAxle: Boolean
        get() = !isOnBoundary || !isRotateAxle

    private fun evaluateCursor(rotation: Float): Int {
        val degree = Math.toDegrees(rotation.toDouble()).roundToInt()
        return when (degree) {
            in 0..<45 -> Cursor.N_RESIZE_CURSOR
            in 45..<90 -> Cursor.NE_RESIZE_CURSOR
            in 90..<135 -> Cursor.E_RESIZE_CURSOR
            in 135..<180 -> Cursor.SE_RESIZE_CURSOR
            in 180..<225 -> Cursor.S_RESIZE_CURSOR
            in 225..<270 -> Cursor.SW_RESIZE_CURSOR
            in 270..<315 -> Cursor.W_RESIZE_CURSOR
            else -> Cursor.NW_RESIZE_CURSOR
        }
    }

    fun pickCursorIcon(rotation: Float): PointerIcon? {
        val cursor = when {
            isRotateAxle -> Cursor.HAND_CURSOR
            isInside -> Cursor.MOVE_CURSOR
            else -> when (corner) {
                CornerSide.NORTH_EAST -> evaluateCursor(rotation + (PI / 4).toFloat())
                CornerSide.NORTH_WEST -> evaluateCursor(rotation + (7 * PI / 4).toFloat())
                CornerSide.SOUTH_EAST -> evaluateCursor(rotation + (3 * PI / 4).toFloat())
                CornerSide.SOUTH_WEST -> evaluateCursor(rotation + (5 * PI / 4).toFloat())
                CornerSide.TOP -> evaluateCursor(rotation)
                CornerSide.BOTTOM -> evaluateCursor(rotation + PI.toFloat())
                CornerSide.LEFT -> evaluateCursor(rotation + (3 * PI / 2).toFloat())
                CornerSide.RIGHT -> evaluateCursor(rotation + (PI / 2).toFloat())
                else -> null
            }
        }
        return cursor?.let(Cursor::getPredefinedCursor)?.let(::PointerIcon)
    }

    fun resetBoundaryPoints() = CanvasItemPointerPosition(isInside = isInside)

    fun toNewBounds(
        item: CanvasItemModel,
        pointerPosition: Offset,
        properties: CanvasPropertiesState,
        canvasCenter: Offset,
    ): Rect = with(item.boundingRect) {

        val scaleFactor = item.scale / properties.canvasScale
        val scaledPosition = (pointerPosition - canvasCenter) * scaleFactor
        val positionOnCanvas = scaledPosition + canvasCenter - properties.panedCanvas

        val finalPosition = CenterPivotRotatedRect.rotatePoint(
            position = positionOnCanvas,
            pivot = center,
            radians = -item.rotateInRadians
        )

        //FIXME: Make a better way to rotate the stuff
        // Yes we can resize the elements but the angle making it difficult to resize them properly
        // holding it for now.

        val newRect = when (corner) {
            CornerSide.NORTH_EAST -> copy(top = finalPosition.y, right = finalPosition.x)
            CornerSide.NORTH_WEST -> copy(left = finalPosition.x, top = finalPosition.y)
            CornerSide.SOUTH_EAST -> copy(right = finalPosition.x, bottom = finalPosition.y)
            CornerSide.SOUTH_WEST -> copy(left = finalPosition.x, bottom = finalPosition.y)
            CornerSide.TOP -> copy(top = finalPosition.y)
            CornerSide.BOTTOM -> copy(bottom = finalPosition.y)
            CornerSide.LEFT -> copy(left = finalPosition.x)
            CornerSide.RIGHT -> copy(right = finalPosition.x)
            else -> this
        }
        return newRect
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