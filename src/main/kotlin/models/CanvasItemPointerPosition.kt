package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerIcon
import org.jetbrains.skiko.Cursor
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.roundToInt

data class CanvasItemPointerPosition(
    val onNECorner: Boolean = false,
    val onNWCorner: Boolean = false,
    val onSECorner: Boolean = false,
    val onSWCorner: Boolean = false,
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
        get() = onNECorner || onNWCorner || onSWCorner || onSECorner

    val isOnBoundary: Boolean
        get() = isCorner || isVertical || isHorizontal

    val isNotOrBoundaryOrAxle: Boolean
        get() = !isOnBoundary || !isRotateAxle


    private fun evaluateCursor(rotation: Float): Int {
        val normalize = ((rotation % (2 * PI)) + 2 * PI) % (2 * PI)

        val angleDegree = Math.toDegrees(normalize).roundToInt()
        return when (angleDegree) {
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
            onNECorner -> evaluateCursor(rotation + (PI / 4).toFloat())
            onNWCorner -> evaluateCursor(rotation - (PI / 4).toFloat())
            onSECorner -> evaluateCursor(rotation + (3 * PI / 4).toFloat())
            onSWCorner -> evaluateCursor(rotation + (5 * PI / 4).toFloat())
            onTBoundary -> evaluateCursor(rotation)
            onBBoundary -> evaluateCursor(rotation + PI.toFloat())
            onLBoundary -> evaluateCursor(rotation + (3 * PI / 2).toFloat())
            onRBoundary -> evaluateCursor(rotation + (PI / 2).toFloat())
            else -> null
        }
        return cursor?.let { PointerIcon(Cursor.getPredefinedCursor(it)) }
    }

    fun resetBoundaryPoints() = CanvasItemPointerPosition(isInside = isInside)

    fun toNewBounds(
        item: CanvasItemModel,
        pointerPosition: Offset,
        properties: CanvasPropertiesState,
        canvasCenter: Offset,
    ): Rect {
        val newRect = with(item.boundingRect) {

            val scaleFactor = item.scale / properties.canvasScale
            val scaledPosition = (pointerPosition - canvasCenter) * scaleFactor
            val finalPos = scaledPosition + canvasCenter - properties.panedCanvas

            val position = CenterPivotRotatedRect.rotatePoint(
                position = finalPos,
                pivot = center,
                radians = -item.rotateInRadians
            )

            // Adjust the bounds based on the rotated position
            when {
                onNECorner -> copy(top = position.y, right = position.x)
                onNWCorner -> copy(left = position.x, top = position.y)
                onSECorner -> copy(right = position.x, bottom = position.y)
                onSWCorner -> copy(left = position.x, bottom = position.y)
                onLBoundary -> copy(left = position.x)
                onTBoundary -> copy(top = position.y)
                onRBoundary -> copy(right = position.x)
                onBBoundary -> copy(bottom = position.y)
                else -> this
            }
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
