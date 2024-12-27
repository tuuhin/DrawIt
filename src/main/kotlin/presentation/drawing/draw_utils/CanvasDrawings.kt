package presentation.drawing.draw_utils

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import mapper.toPathEffect
import mapper.width
import models.CanvasPropertiesState
import models.actions.CanvasDrawAction
import models.canvas.BackgroundFillOptions
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption

fun DrawScope.drawCanvasObjects(
    boundingRect: Rect,
    action: CanvasDrawAction,
    properties: CanvasPropertiesState,
    strokeColor: Color = Color.Red,
    fillColor: Color = Color.Yellow,
    fillMode: BackgroundFillOptions = BackgroundFillOptions.NONE,
    strokeWidthOption: StrokeWidthOption = StrokeWidthOption.THIN,
    pathEffectOptions: PathEffectOptions = PathEffectOptions.SOLID,
    alpha: Float = 1f,
    isRounded: Boolean = false,
    hasBoundary: Boolean = false,
    boundaryColor: Color = Color.Yellow,
) {
    val basePath = Path()

    val stroke = Stroke(
        width = strokeWidthOption.width.toPx(),
        cap = StrokeCap.Round,
        pathEffect = pathEffectOptions.toPathEffect(
            dottedInterval = 6.dp.toPx(),
            dashedInterval = 12.dp.toPx()
        )
    )

    // draw point at the center
    // TODO : Remove it later
    drawCircle(color = Color.Red, radius = 2.dp.toPx(), center = boundingRect.center)

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
            drawBackground(
                path = path,
                boundingRect = boundingRect,
                fillMode = fillMode,
                strokeWidthOption = strokeWidthOption,
                alpha = alpha,
                fillColor = fillColor
            )
            // draw content
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
            drawBackground(
                path = path,
                boundingRect = boundingRect,
                fillMode = fillMode,
                strokeWidthOption = strokeWidthOption,
                alpha = alpha,
                fillColor = fillColor
            )
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
            drawBackground(
                path = path,
                boundingRect = boundingRect,
                fillMode = fillMode,
                alpha = alpha,
                strokeWidthOption = strokeWidthOption,
                fillColor = fillColor
            )
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

        CanvasDrawAction.ACTION_ARROW -> {
            drawArrow(boundingRect = boundingRect, strokeColor = strokeColor, stroke = stroke, alpha = alpha)
        }

        CanvasDrawAction.ACTION_DRAW -> {
            // later implement the draw as its special
        }

        else -> {}
    }
    // skip it no boundary
    if (!hasBoundary) return

    drawSelectedBoundary(
        action = action,
        boundingRect = boundingRect,
        properties = properties,
        boundaryColor = boundaryColor
    )
}


fun Path.moveTo(offset: Offset) = moveTo(offset.x, offset.y)
fun Path.lineTo(offset: Offset) = lineTo(offset.x, offset.y)



