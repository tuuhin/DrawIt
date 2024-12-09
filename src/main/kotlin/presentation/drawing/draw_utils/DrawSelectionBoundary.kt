package presentation.drawing.draw_utils

import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import models.CanvasItemModel.Companion.AXLE_POSITION_OFFSET
import models.CanvasPropertiesState
import models.actions.CanvasDrawAction

fun DrawScope.drawSelectedBoundary(
    action: CanvasDrawAction,
    boundingRect: Rect,
    properties: CanvasPropertiesState,
    boundaryColor: Color,
) {
    if (!action.canHaveBoundary) return

    with(boundingRect.inflate(2.dp.toPx())) {
        val boundaryBox = (3.dp.toPx() / properties.canvasScale).toSize()
        // boundary
        val boundary = Path().apply {
            moveTo(topLeft + Offset(boundaryBox.width, 0f))
            lineTo(topRight - Offset(boundaryBox.width, 0f))

            moveTo(topRight + Offset(0f, boundaryBox.height))
            lineTo(bottomRight - Offset(0f, boundaryBox.height))

            moveTo(bottomRight - Offset(boundaryBox.width, 0f))
            lineTo(bottomLeft + Offset(boundaryBox.width, 0f))

            moveTo(bottomLeft - Offset(0f, boundaryBox.height))
            lineTo(topLeft + Offset(0f, boundaryBox.height))
        }
        val outlinePath = Path().apply {
            addRoundRect(topLeft.calculateRectFromCenter(boundaryBox))
            addRoundRect(topRight.calculateRectFromCenter(boundaryBox))
            addRoundRect(bottomLeft.calculateRectFromCenter(boundaryBox))
            addRoundRect(bottomRight.calculateRectFromCenter(boundaryBox))
            addPath(boundary)
            // draw rotate icon
            val axleOffPosition = Offset(0f, AXLE_POSITION_OFFSET)
            addRoundRect((topCenter - axleOffPosition).calculateRectFromCenter(boundaryBox))
        }
        drawPath(
            path = outlinePath,
            color = boundaryColor,
            style = Stroke(
                width = 1.dp.toPx() / properties.canvasScale,
                join = StrokeJoin.Miter
            )
        )
    }
}

private fun Offset.calculateRectFromCenter(size: Size): RoundRect {
    val half = Offset(size.width, size.height)
    return RoundRect(
        rect = Rect(topLeft = this - half, bottomRight = this + half),
        cornerRadius = CornerRadius(2f, 2f)
    )
}


private fun Float.toSize() = Size(this, this)