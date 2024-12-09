package presentation.drawing.draw_utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

fun DrawScope.drawArrow(
    boundingRect: Rect,
    strokeColor: Color = Color.Red,
    stroke: Stroke = Stroke(),
    alpha: Float = 1f,
) {
    val length = with(boundingRect) { (topLeft - bottomRight).getDistance() }
    val arrowSize = 16.dp.toPx().let { size -> if (length >= size) size else length }
    val ratio = arrowSize / length
    val arrowStart = with(boundingRect) { topLeft.divideSegment(bottomRight, ratio) }

    drawLine(
        color = strokeColor,
        start = boundingRect.topLeft,
        end = boundingRect.bottomRight,
        strokeWidth = stroke.width,
        pathEffect = stroke.pathEffect,
        cap = stroke.cap,
        alpha = alpha,
    )
    rotate(degrees = 30f, pivot = boundingRect.bottomRight) {
        drawLine(
            color = strokeColor,
            start = arrowStart,
            end = boundingRect.bottomRight,
            strokeWidth = stroke.width,
            pathEffect = stroke.pathEffect,
            cap = stroke.cap,
            alpha = alpha,
        )
    }
    rotate(degrees = -30f, pivot = boundingRect.bottomRight) {
        drawLine(
            color = strokeColor,
            start = arrowStart,
            end = boundingRect.bottomRight,
            strokeWidth = stroke.width,
            pathEffect = stroke.pathEffect,
            cap = stroke.cap,
            alpha = alpha,
        )
    }
}

private fun Offset.divideSegment(other: Offset, ratio: Float): Offset =
    Offset((1 - ratio) * other.x + ratio * this.x, (1 - ratio) * other.y + ratio * this.y)