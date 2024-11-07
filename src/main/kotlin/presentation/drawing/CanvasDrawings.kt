package presentation.drawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import models.actions.CanvasDrawAction
import kotlin.math.pow
import kotlin.math.sqrt

fun DrawScope.drawCanvasObjects(
    boundingRect: Rect,
    action: CanvasDrawAction,
    strokeColor: Color = Color.Red,
    fillColor: Color = Color.Yellow,
    stroke: Stroke = Stroke(),
    alpha: Float = 1f,
    isRounded: Boolean = false,
) {
    val basePath = Path()

    when (action) {
        // ellipse
        CanvasDrawAction.ACTION_ELLIPSE -> {
            // an outlined one
            val path = basePath.apply {
                reset()
                addOval(oval = boundingRect)
                close()
            }
            // draw background
            if (fillColor != Color.Transparent) {
                drawPath(
                    path = path,
                    style = Fill,
                    alpha = alpha,
                    color = fillColor,
                )
            }
            drawPath(
                path = path,
                style = stroke,
                alpha = alpha,
                color = strokeColor,
            )
        }
        // rect or rounded rect
        CanvasDrawAction.ACTION_RECT -> {

            val corner = if (isRounded) CornerRadius(10.dp.toPx(), 10.dp.toPx()) else CornerRadius.Zero
            val path = basePath.apply {
                reset()
                addRoundRect(roundRect = RoundRect(boundingRect, corner))
                close()
            }
            // draw background
            if (fillColor != Color.Transparent) {
                drawPath(
                    path = path,
                    style = Fill,
                    alpha = alpha,
                    color = fillColor,
                )
            }
            drawPath(
                path = path,
                style = stroke,
                alpha = alpha,
                color = strokeColor,
            )
        }
        // kite
        CanvasDrawAction.ACTION_DIAMOND -> {
            val widthMultiplier = if (boundingRect.width < 0) -1 else 1
            val heightMultiplier = if (boundingRect.height < 0) -1 else 1

            val corner = if (isRounded) with(boundingRect) {
                val radius = 5.dp.toPx()
                CornerRadius(radius * widthMultiplier, radius * heightMultiplier)
            } else CornerRadius.Zero

            val path = basePath.apply {
                reset()
                with(boundingRect.topCenter) {
                    moveTo(x - corner.x, y + corner.y)
                    cubicTo(x - corner.x, y + corner.y, x, y, x + corner.x, y + corner.y)
                }
                with(boundingRect.centerRight) {
                    lineTo(x - corner.x, y - corner.y)
                    cubicTo(x - corner.x, y - corner.y, x, y, x - corner.x, y + corner.y)
                }
                with(boundingRect.bottomCenter) {
                    lineTo(x + corner.x, y - corner.y)
                    cubicTo(x + corner.x, y - corner.y, x, y, x - corner.x, y - corner.y)
                }
                with(boundingRect.centerLeft) {
                    lineTo(x + corner.x, y + corner.y)
                    cubicTo(x + corner.x, y + corner.y, x, y, x + corner.x, y - corner.y)
                }
                close()
            }
            // draw background
            if (fillColor != Color.Transparent) {
                drawPath(
                    path = path,
                    color = fillColor,
                    style = Fill,
                    alpha = alpha,
                )
            }
            // draw the outline
            drawPath(path = path, color = strokeColor, style = stroke, alpha = alpha)
        }
        // draw a plain line and it doesn't have a background
        CanvasDrawAction.ACTION_LINE -> {
            val path = basePath.apply {
                reset()
                moveTo(boundingRect.topLeft)
                lineTo(boundingRect.bottomRight)
                close()
            }
            drawPath(path, color = strokeColor, style = stroke, alpha = alpha)
        }
        // draw arrow change it to path mode later
        CanvasDrawAction.ACTION_ARROW -> {

            val length = with(boundingRect) { topLeft.distanceBetween(bottomRight) }
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

        else -> {}
    }
}

private fun Path.moveTo(offset: Offset) = moveTo(offset.x, offset.y)
private fun Path.lineTo(offset: Offset) = lineTo(offset.x, offset.y)

private fun Offset.distanceBetween(other: Offset): Float =
    sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2))

private fun Offset.divideSegment(other: Offset, ratio: Float): Offset =
    Offset((1 - ratio) * other.x + ratio * this.x, (1 - ratio) * other.y + ratio * this.y)
