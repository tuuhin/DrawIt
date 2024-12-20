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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.style_option_fill_color
import mapper.backgroundColor
import models.canvas.CanvasColorOptions
import org.jetbrains.compose.resources.stringResource
import utils.thenIf

@Composable
fun BackgroundColorPicker(
    backGroundColor: CanvasColorOptions,
    onBackgroundColorChange: (CanvasColorOptions) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(Res.string.style_option_fill_color),
            color = headingColor,
            style = headingStyle,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CanvasColorOptions.entries.forEach { color ->
                Box(
                    modifier = Modifier.size(32.dp)
                        .clip(MaterialTheme.shapes.small)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(onClick = { onBackgroundColorChange(color) })
                        .drawWithCache {
                            val rect = Rect(Offset.Zero, size).deflate(4.dp.toPx())
                            val path = Path().apply {
                                addRoundRect(RoundRect(rect, CornerRadius(4.dp.toPx())))
                            }
                            val backgroundColor = if (color == CanvasColorOptions.BASE) Color.Gray.copy(alpha = .5f)
                            else color.backgroundColor

                            onDrawBehind {
                                drawPath(path = path, color = backgroundColor, style = Fill)
                            }
                        }.thenIf(
                            condition = color == backGroundColor,
                            modifier = Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            )
                        ),
                )
            }
        }

    }
}
