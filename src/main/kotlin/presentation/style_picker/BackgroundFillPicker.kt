package presentation.style_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.style_option_background_fill_type
import models.canvas.BackgroundFillOptions
import org.jetbrains.compose.resources.stringResource

private const val NO_OF_LINES = 5

@Composable
fun BackgroundFillPicker(
    showPicker: Boolean,
    fillOption: BackgroundFillOptions,
    onFillOptionChange: (BackgroundFillOptions) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedContainer: Color = MaterialTheme.colorScheme.primaryContainer,
    onSelectedContainer: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unSelectedContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    onUnSelectedContainer: Color = MaterialTheme.colorScheme.onSurface,
) {
    AnimatedVisibility(visible = showPicker, modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(Res.string.style_option_background_fill_type),
                color = headingColor,
                style = headingStyle,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                BackgroundFillOptions.entries.forEach { option ->
                    val isSelected = option == fillOption
                    val color = if (isSelected) onSelectedContainer else onUnSelectedContainer
                    Box(
                        modifier = Modifier.size(32.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(if (isSelected) selectedContainer else unSelectedContainerColor)
                            .clickable { onFillOptionChange(option) }
                    ) {
                        Spacer(
                            modifier = Modifier.matchParentSize()
                                .padding(6.dp)
                                .drawWithCache {

                                    val path = Path().apply {
                                        addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(4.dp.toPx())))
                                    }

                                    val heightRatio = size.height / NO_OF_LINES
                                    val widthRatio = size.width / NO_OF_LINES

                                    onDrawBehind {
                                        when (option) {
                                            BackgroundFillOptions.SOLID -> drawPath(
                                                path = path,
                                                color = color,
                                                style = Fill
                                            )

                                            BackgroundFillOptions.CROSS_HATCH -> clipPath(path) {
                                                drawPath(
                                                    path = path,
                                                    color = color,
                                                    style = Stroke(width = 1.dp.toPx())
                                                )
                                                repeat(NO_OF_LINES) {
                                                    // horizontal
                                                    val hStart = Offset(0f, heightRatio * it)
                                                    val hEnd = Offset(size.width, heightRatio * it)
                                                    drawLine(color = color, start = hStart, end = hEnd)
                                                    //vertical
                                                    val vStart = Offset(widthRatio * it, 0f)
                                                    val vEnd = Offset(widthRatio * it, size.height)
                                                    drawLine(color = color, start = vStart, end = vEnd)
                                                }
                                            }

                                            BackgroundFillOptions.SINGLE_HATCH -> clipPath(path) {
                                                drawPath(
                                                    path = path,
                                                    color = color,
                                                    style = Stroke(width = 1.dp.toPx())
                                                )
                                                repeat(NO_OF_LINES) {
                                                    val start = Offset(0f, heightRatio * it)
                                                    val end = Offset(size.width, heightRatio * it)
                                                    drawLine(color = color, start = start, end = end)
                                                }
                                            }
                                        }
                                    }
                                },
                        )
                    }
                }
            }
        }
    }
}