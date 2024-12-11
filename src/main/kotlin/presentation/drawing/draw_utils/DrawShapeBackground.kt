package presentation.drawing.draw_utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import mapper.width
import models.canvas.BackgroundFillOptions
import models.canvas.StrokeWidthOption

fun DrawScope.drawBackground(
    path: Path,
    boundingRect: Rect,
    fillMode: BackgroundFillOptions? = null,
    strokeWidthOption: StrokeWidthOption = StrokeWidthOption.THIN,
    alpha: Float,
    fillColor: Color,
) {
    if (fillMode == null || fillColor == Color.Transparent) return
    when (fillMode) {

        BackgroundFillOptions.SOLID -> drawPath(
            path = path,
            style = Fill,
            alpha = alpha,
            color = fillColor,
        )

        BackgroundFillOptions.CROSS_HATCH -> clipPath(path) {

            val difference = strokeWidthOption.width.toPx() * 2f
            val noOfHorizontalLines = (boundingRect.height / difference).toInt()
            val noOfVerticalLines = (boundingRect.width / difference).toInt()

            // horizontal
            repeat(noOfHorizontalLines) {
                val hStart = Offset(boundingRect.left, boundingRect.top + difference * it)
                val hEnd = Offset(boundingRect.right, boundingRect.top + difference * it)
                drawLine(color = fillColor, start = hStart, end = hEnd)
            }
            //vertical
            repeat(noOfVerticalLines) {
                val vStart = Offset(boundingRect.left + difference * it, boundingRect.top)
                val vEnd = Offset(boundingRect.left + difference * it, boundingRect.bottom)
                drawLine(color = fillColor, start = vStart, end = vEnd)
            }
        }

        BackgroundFillOptions.SINGLE_HATCH -> clipPath(path) {

            val difference = strokeWidthOption.width.toPx() * 2f
            val noOfHorizontalLines = (boundingRect.height / difference).toInt()

            // horizontal
            repeat(noOfHorizontalLines) {
                val hStart = Offset(boundingRect.left, boundingRect.top + difference * it)
                val hEnd = Offset(boundingRect.right, boundingRect.top + difference * it)
                drawLine(color = fillColor, start = hStart, end = hEnd)
            }
        }
        // Nothing for none
        BackgroundFillOptions.NONE -> {}
    }
}

