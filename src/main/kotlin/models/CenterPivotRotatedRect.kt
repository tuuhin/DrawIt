package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
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

    private val topLeft: Offset
        get() = rotatePoint(rect.topLeft, rect.center, normalizedRadians)

    private val topRight: Offset
        get() = rotatePoint(rect.topRight, rect.center, normalizedRadians)

    private val bottomLeft: Offset
        get() = rotatePoint(rect.bottomLeft, rect.center, normalizedRadians)

    private val bottomRight: Offset
        get() = rotatePoint(rect.bottomRight, rect.center, normalizedRadians)


    fun contains(offset: Offset): Boolean {
        val rotatedPoint = rotatePoint(offset, center, -normalizedRadians)
        return rect.contains(rotatedPoint)
    }

    fun onTopBoundary(offset: Offset): Boolean {
        val topEdge = when (normalizedRadians) {
            in 0.0..(PI / 2) -> Pair(topLeft, topRight)
            in (PI / 2).toFloat()..PI.toFloat() -> Pair(topRight, bottomRight)
            in PI.toFloat()..(3 * PI / 2).toFloat() -> Pair(bottomRight, bottomLeft)
            else -> Pair(bottomLeft, topLeft)
        }
        return isPointNearLine(topEdge.first, topEdge.second, offset)
    }

    fun onBottomBoundary(offset: Offset): Boolean {
        val bottomEdge = when (normalizedRadians) {
            in 0.0..(PI / 2) -> Pair(bottomLeft, bottomRight)
            in (PI / 2).toFloat()..PI.toFloat() -> Pair(topLeft, bottomLeft)
            in PI.toFloat()..(3 * PI / 2).toFloat() -> Pair(topLeft, topRight)
            else -> Pair(topRight, bottomRight)
        }
        return isPointNearLine(bottomEdge.first, bottomEdge.second, offset)
    }

    fun onLeftBoundary(offset: Offset): Boolean {
        val leftEdge = when (normalizedRadians) {
            in 0.0..(PI / 2) -> Pair(topLeft, bottomLeft)
            in (PI / 2).toFloat()..PI.toFloat() -> Pair(topLeft, topRight)
            in PI.toFloat()..(3 * PI / 2).toFloat() -> Pair(topRight, bottomRight)
            else -> Pair(bottomLeft, bottomRight)
        }
        return isPointNearLine(leftEdge.first, leftEdge.second, offset)
    }

    fun onRightBoundary(offset: Offset): Boolean {
        val rightEdge = when (normalizedRadians) {
            in 0.0..(PI / 2) -> Pair(topRight, bottomRight)
            in (PI / 2).toFloat()..PI.toFloat() -> Pair(bottomLeft, bottomRight)
            in PI.toFloat()..(3 * PI / 2).toFloat() -> Pair(bottomLeft, topLeft)
            else -> Pair(topLeft, topRight)
        }
        return isPointNearLine(rightEdge.first, rightEdge.second, offset)
    }

    fun isOnTopLeftBoundary(offset: Offset): Boolean {
        val corner = when (normalizedRadians) {
            in 0f..(PI / 2).toFloat() -> topLeft
            in (PI / 2).toFloat()..PI.toFloat() -> bottomLeft
            in PI.toFloat()..(3 * PI / 2).toFloat() -> bottomRight
            else -> topRight
        }
        return (offset - corner).getDistance() <= MAX_DISTANCE
    }

    fun isOnTopRightBoundary(offset: Offset): Boolean {
        val corner = when (normalizedRadians) {
            in 0f..(PI / 2).toFloat() -> topRight
            in (PI / 2).toFloat()..PI.toFloat() -> topLeft
            in PI.toFloat()..(3 * PI / 2).toFloat() -> bottomLeft
            else -> bottomRight
        }
        return (offset - corner).getDistance() <= MAX_DISTANCE
    }

    fun isOnBottomLeftBoundary(offset: Offset): Boolean {
        val corner = when (normalizedRadians) {
            in 0f..(PI / 2).toFloat() -> bottomLeft
            in (PI / 2).toFloat()..PI.toFloat() -> topLeft
            in PI.toFloat()..(3 * PI / 2).toFloat() -> bottomLeft
            else -> bottomRight
        }
        return (offset - corner).getDistance() <= MAX_DISTANCE
    }

    fun isOnBottomRightBoundary(offset: Offset): Boolean {
        val corner = when (normalizedRadians) {
            in 0f..(PI / 2).toFloat() -> bottomRight
            in (PI / 2).toFloat()..PI.toFloat() -> bottomLeft
            in PI.toFloat()..(3 * PI / 2).toFloat() -> topLeft
            else -> topRight
        }
        return (offset - corner).getDistance() <= MAX_DISTANCE
    }

    fun onRotationAxlePosition(offset: Offset, density: Density): Boolean {
        val position = with(density) {
            Offset(rect.center.x, rect.top - AXLE_POSITION_OFFSET)
        }
        val rotatedPos = rotatePoint(position, rect.center, radians = normalizedRadians)
        return (offset - rotatedPos).getDistance() < 2 * MAX_DISTANCE
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


    private fun isPointNearLine(start: Offset, end: Offset, point: Offset): Boolean {
        val lineVector = end - start
        val pointVector = point - start

        // Calculate the projection of the point onto the line
        val lineLengthSquared = lineVector.x * lineVector.x + lineVector.y * lineVector.y
        if (lineLengthSquared == 0f) return false // Line segment is a point

        val t = (pointVector.x * lineVector.x + pointVector.y * lineVector.y) / lineLengthSquared

        // Clamp t to the range [0, 1] to stay within the segment
        val clampedT = t.coerceIn(0f, 1f)

        // Find the closest point on the line segment
        val closestPoint = Offset(start.x + clampedT * lineVector.x, start.y + clampedT * lineVector.y)
        // Check the distance to the closest point
        return (point - closestPoint).getDistance() <= MAX_DISTANCE
    }

    private fun rotatePoint(position: Offset, pivot: Offset, radians: Float): Offset {
        val dx = position.x - pivot.x
        val dy = position.y - pivot.y
        val rotatedX = cos(radians) * dx - sin(radians) * dy + pivot.x
        val rotatedY = sin(radians) * dx + cos(radians) * dy + pivot.y
        return Offset(rotatedX, rotatedY)
    }

    companion object {
        fun fromRectAndAngle(rect: Rect, radians: Float) = CenterPivotRotatedRect(rect = rect, radians = radians)
    }
}