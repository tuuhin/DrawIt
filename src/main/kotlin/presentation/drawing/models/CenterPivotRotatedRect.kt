package presentation.drawing.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import models.CanvasItemModel.Companion.AXLE_POSITION_OFFSET
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val MAX_DISTANCE = 5f

data class CenterPivotRotatedRect(
    private val rect: Rect,
    private val radians: Float = .0f,
) {

    private val normalizedRadians: Float
        get() = radians % (2 * PI).toFloat()


    private val center: Offset = rect.center

    fun contains(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return rect.contains(rotatedPoint)
    }

    private fun onTopBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointNearLine(topLeft, topRight, rotatedPoint) }
    }

    private fun onBottomBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointNearLine(bottomLeft, bottomRight, rotatedPoint) }
    }

    private fun onLeftBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointNearLine(topLeft, bottomLeft, rotatedPoint) }
    }

    private fun onRightBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointNearLine(topRight, bottomRight, rotatedPoint) }
    }

    private fun isOnTopLeftBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointCloser(rotatedPoint, topLeft) }
    }

    private fun isOnTopRightBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointCloser(rotatedPoint, topRight) }
    }

    private fun isOnBottomLeftBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointCloser(rotatedPoint, bottomLeft) }
    }

    private fun isOnBottomRightBoundary(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return with(rect) { isPointCloser(rotatedPoint, bottomRight) }
    }

    private fun onRotationAxlePosition(offset: Offset, density: Density): Boolean {
        val unRotatedAxlePosition = with(density) { Offset(rect.center.x, rect.top - AXLE_POSITION_OFFSET) }
        val rotatedAxlePosition = rotatePoint(unRotatedAxlePosition, center, normalizedRadians)
        return (offset - rotatedAxlePosition).getDistance() <= with(density) { 10.dp.toPx() }
    }

    fun insideInteractionBoundary(
        offset: Offset,
        inflateDelta: Float = MAX_DISTANCE,
        deflateDelta: Float = MAX_DISTANCE,
    ): Boolean {
        val inflatedRect = this.copy(rect = rect.inflate(inflateDelta))
        val deflatedRect = this.copy(rect = rect.deflate(deflateDelta))

        return inflatedRect.contains(offset) && !deflatedRect.contains(offset)
    }

    fun updatePointerPosition(position: Offset, density: Density): CanvasItemPointerPosition {
        return CanvasItemPointerPosition(
            onNECorner = isOnTopRightBoundary(position),
            onNWCorner = isOnTopLeftBoundary(position),
            onSWCorner = isOnBottomLeftBoundary(position),
            onSECorner = isOnBottomRightBoundary(position),
            onTBoundary = onTopBoundary(position),
            onBBoundary = onBottomBoundary(position),
            onLBoundary = onLeftBoundary(position),
            onRBoundary = onRightBoundary(position),
            isRotateAxle = onRotationAxlePosition(position, density)
        )
    }


    private fun isPointNearLine(start: Offset, end: Offset, point: Offset): Boolean {
        val lineVector = end - start
        val pointVector = point - start

        // Calculate the projection of the point onto the line
        val lengthSquared = lineVector.getDistanceSquared()
        if (lengthSquared == 0f) return false // Line segment is a point

        val t = (pointVector.x * lineVector.x + pointVector.y * lineVector.y) / lengthSquared

        // Clamp t to the range [0, 1] to stay within the segment
        val clampedT = t.coerceIn(0f, 1f)

        // Find the closest point on the line segment
        val closestPoint = Offset(start.x + clampedT * lineVector.x, start.y + clampedT * lineVector.y)
        // Check the distance to the closest point
        return (point - closestPoint).getDistance() <= MAX_DISTANCE
    }

    private fun isPointCloser(reference: Offset, point: Offset, distance: Float = 5f) =
        (reference - point).getDistance() <= distance

    companion object {

        fun fromRectAndAngle(rect: Rect, radians: Float) = CenterPivotRotatedRect(rect = rect, radians = radians)

        fun rotatePoint(position: Offset, pivot: Offset, radians: Float): Offset {
            val dx = position.x - pivot.x
            val dy = position.y - pivot.y
            val rotatedX = cos(radians) * dx - sin(radians) * dy + pivot.x
            val rotatedY = sin(radians) * dx + cos(radians) * dy + pivot.y
            return Offset(rotatedX, rotatedY)
        }
    }
}