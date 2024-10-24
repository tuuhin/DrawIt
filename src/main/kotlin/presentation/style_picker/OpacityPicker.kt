package presentation.style_picker

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ui.DrawItAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpacityPicker(
    level: Float,
    onLevelChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
    colors: SliderColors = SliderDefaults.colors(),
) {
    Column(modifier = modifier) {
        Text(
            text = "Opacity",
            color = headingColor,
            style = headingStyle
        )
        Slider(
            value = level,
            thumb = {
                Spacer(
                    modifier = Modifier.size(20.dp)
                        .drawBehind {
                            drawCircle(color = colors.thumbColor, center = center, radius = 8.dp.toPx())
                        }
                )
            },
            track = { state ->
                Spacer(
                    modifier = Modifier.fillMaxWidth().height(12.dp)
                        .drawBehind {
                            drawLine(
                                color = colors.activeTrackColor,
                                start = Offset(0f, size.height * .5f),
                                end = Offset(size.width * state.value, size.height * .5f),
                                strokeWidth = 6.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = colors.inactiveTrackColor,
                                start = Offset(size.width * state.value, size.height * .5f),
                                end = Offset(size.width, size.height * .5f),
                                strokeWidth = 6.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        },
                )
            },
            onValueChange = onLevelChange,
            colors = colors,
        )
    }
}

@Preview
@Composable
fun OpacityPickerPreview() = DrawItAppTheme {
    OpacityPicker(level = 0.5f, onLevelChange = {})
}