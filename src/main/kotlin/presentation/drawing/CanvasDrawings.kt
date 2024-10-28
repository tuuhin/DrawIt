package presentation.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
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
    style: DrawStyle = Stroke(),
    alpha: Float = 1f,
) {
    when (action) {
        //oval
        CanvasDrawAction.ACTION_ELLIPSE -> {
            // an outlined one
            drawOval(
                color = strokeColor,
                topLeft = boundingRect.topLeft,
                size = boundingRect.size,
                style = style,
                alpha = alpha
            )
        }
        // basic rect
        CanvasDrawAction.ACTION_RECT -> {
            drawRoundRect(
                color = strokeColor,
                topLeft = boundingRect.topLeft,
                size = boundingRect.size,
                style = style,
                alpha = alpha,
            )
        }
        // its tilted rhombus
        CanvasDrawAction.ACTION_DIAMOND -> {
            val path = Path().apply {
                reset()
                with(boundingRect.topCenter) {
                    moveTo(x, y)
                }
                // add the points
                listOf(
                    boundingRect.centerRight,
                    boundingRect.bottomCenter,
                    boundingRect.centerLeft,
                    boundingRect.topCenter
                ).forEach { lineTo(it.x, it.y) }
            }
            // draw the path
            drawPath(path, color = strokeColor, style = style, alpha = alpha)
        }
        // draw a plain line
        CanvasDrawAction.ACTION_LINE -> {
            (style as? Stroke)?.let { stroke ->
                drawLine(
                    color = strokeColor,
                    start = boundingRect.topLeft,
                    end = boundingRect.bottomRight,
                    strokeWidth = stroke.width,
                    pathEffect = stroke.pathEffect,
                    cap = stroke.cap,
                    alpha = alpha,
                )
            } ?: drawLine(
                color = strokeColor,
                start = boundingRect.topLeft,
                end = boundingRect.bottomRight,
                alpha = alpha,
            )
        }
        // draw arrow
        CanvasDrawAction.ACTION_ARROW -> {

            val startX = boundingRect.topLeft.x
            val startY = boundingRect.topLeft.y

            val endX = boundingRect.bottomRight.x
            val endY = boundingRect.bottomRight.y

            val length = sqrt((startX - endX).pow(2) + (startY - endY).pow(2))
            val arrowSize = 16.dp.toPx().let { size -> if (length >= size) size else length }

            val startOffset = Offset(
                ((length - arrowSize) * endX + arrowSize * startX) / length,
                ((length - arrowSize) * endY + arrowSize * startY) / length
            )

            (style as? Stroke)?.let { stroke ->
                drawLine(
                    color = strokeColor,
                    start = boundingRect.topLeft,
                    end = boundingRect.bottomRight,
                    strokeWidth = stroke.width,
                    pathEffect = stroke.pathEffect,
                    cap = stroke.cap,
                    alpha = alpha,
                )
            } ?: drawLine(
                color = strokeColor,
                start = boundingRect.topLeft,
                end = boundingRect.bottomRight,
                alpha = alpha,
            )

            rotate(degrees = 30f, pivot = boundingRect.bottomRight) {
                (style as? Stroke)?.let { stroke ->
                    drawLine(
                        color = strokeColor,
                        start = startOffset,
                        end = boundingRect.bottomRight,
                        strokeWidth = stroke.width,
                        pathEffect = stroke.pathEffect,
                        cap = stroke.cap,
                        alpha = alpha,
                    )
                } ?: drawLine(
                    color = strokeColor,
                    start = startOffset,
                    end = boundingRect.bottomRight,
                    alpha = alpha,
                )
            }
            rotate(degrees = -30f, pivot = boundingRect.bottomRight) {
                (style as? Stroke)?.let { stroke ->
                    drawLine(
                        color = strokeColor,
                        start = startOffset,
                        end = boundingRect.bottomRight,
                        strokeWidth = stroke.width,
                        pathEffect = stroke.pathEffect,
                        cap = stroke.cap,
                        alpha = alpha,
                    )
                } ?: drawLine(
                    color = strokeColor,
                    start = startOffset,
                    end = boundingRect.bottomRight,
                    alpha = alpha,
                )
            }
        }

        else -> {}
    }
}