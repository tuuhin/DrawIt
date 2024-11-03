package presentation.drawing

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.drawGraphLines(showGraph: Boolean = false) = composed {
    if (!showGraph) return@composed Modifier

    val lineColor = MaterialTheme.colorScheme.onSurface
    drawBehind {

        val strokeWidth = .2.dp.toPx()
        val space = 25.dp.toPx()

        repeat(size.width.toInt() / space.toInt()) {
            drawLine(
                color = lineColor,
                start = Offset(space * it, 0f),
                end = Offset(space * it, size.height),
                strokeWidth = strokeWidth
            )
        }
        repeat(size.height.toInt() / space.toInt()) {
            drawLine(
                color = lineColor,
                start = Offset(0f, space * it),
                end = Offset(size.width, space * it),
                strokeWidth = strokeWidth
            )
        }
    }
}