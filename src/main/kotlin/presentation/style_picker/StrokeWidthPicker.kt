package presentation.style_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.style_option_stroke_width
import models.canvas.StrokeWidthOption
import org.jetbrains.compose.resources.stringResource

@Composable
fun StrokeWidthPicker(
    canvasStrokeOption: StrokeWidthOption,
    onStrokeWidthChange: (StrokeWidthOption) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedContainer: Color = MaterialTheme.colorScheme.primaryContainer,
    onSelectedContainer: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unSelectedContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    onUnSelectedContainer: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(Res.string.style_option_stroke_width),
            color = headingColor,
            style = headingStyle
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            StrokeWidthOption.entries.forEach { strokeOption ->

                val strokeColor = if (canvasStrokeOption == strokeOption) onSelectedContainer
                else onUnSelectedContainer

                val boxColor = if (canvasStrokeOption == strokeOption) selectedContainer
                else unSelectedContainerColor

                Box(
                    modifier = Modifier.size(32.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clip(MaterialTheme.shapes.small)
                        .background(boxColor)
                        .clickable(onClick = { onStrokeWidthChange(strokeOption) })
                        .padding(4.dp)
                        .drawBehind {
                            val strokeWidth = when (strokeOption) {
                                StrokeWidthOption.THIN -> 1.dp
                                StrokeWidthOption.BOLD -> 2.dp
                                StrokeWidthOption.EXTRA_BOLD -> 3.dp
                            }
                            drawLine(
                                color = strokeColor,
                                start = Offset(size.width * .25f, size.height * .5f),
                                end = Offset(size.width * .75f, size.height * .5f),
                                strokeWidth = strokeWidth.toPx(),
                                cap = StrokeCap.Round
                            )
                        },
                )
            }
        }
    }
}