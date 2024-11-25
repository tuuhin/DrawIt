package presentation.style_picker

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.style_option_stroke_color
import mapper.foregroundColor
import models.canvas.CanvasColorOptions
import org.jetbrains.compose.resources.stringResource
import utils.thenIf

@Composable
fun StrokeColorPicker(
    strokeColor: CanvasColorOptions,
    onStrokeColorChange: (CanvasColorOptions) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
    shape: Shape = MaterialTheme.shapes.small,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(Res.string.style_option_stroke_color),
            color = headingColor,
            style = headingStyle,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            CanvasColorOptions.entries.forEach { color ->
                Box(
                    modifier = Modifier.size(32.dp)
                        .clip(shape)
                        .clickable(onClick = { onStrokeColorChange(color) })
                        .pointerHoverIcon(PointerIcon.Hand)
                        .drawWithCache {

                            val rect = Rect(Offset.Zero, size).deflate(4.dp.toPx())
                            val path = Path().apply {
                                addRoundRect(RoundRect(rect, CornerRadius(4.dp.toPx())))
                            }
                            onDrawBehind {
                                drawPath(path = path, color = color.foregroundColor, style = Fill)
                            }

                        }.thenIf(
                            condition = color == strokeColor,
                            modifier = Modifier.border(width = 1.dp, color = borderColor, shape = shape)
                        )
                )
            }
        }
    }
}