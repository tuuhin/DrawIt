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
import mapper.toPathEffect
import mapper.width
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption

@Composable
fun PathEffectPicker(
    pathEffect: PathEffectOptions,
    onPathEffectChange: (PathEffectOptions) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Stroke Style",
            color = headingColor,
            style = headingStyle,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PathEffectOptions.entries.forEach { pathEffectOption ->

                val onBoxColor = if (pathEffect == pathEffectOption)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface

                val boxColor = if (pathEffect == pathEffectOption)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceContainerHigh

                Box(
                    modifier = Modifier.size(32.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clip(MaterialTheme.shapes.small)
                        .clickable(onClick = { onPathEffectChange(pathEffectOption) })
                        .background(boxColor)
                        .padding(4.dp)
                        .drawBehind {

                            val lineWidth = size.width * .5f

                            drawLine(
                                color = onBoxColor,
                                start = Offset(size.width * .25f, size.height * .5f),
                                end = Offset(size.width * .75f, size.height * .5f),
                                strokeWidth = StrokeWidthOption.THIN.width.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = pathEffectOption.toPathEffect(
                                    dashedIntervals = floatArrayOf(lineWidth * .3f, lineWidth * .4f),
                                    dottedIntervals = floatArrayOf(lineWidth * .2f, lineWidth * .2f)
                                )
                            )
                        },
                )
            }
        }
    }
}