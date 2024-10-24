package presentation.style_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import mapper.foregroundColor
import models.canvas.CanvasColorOptions
import utils.thenIf

@Composable
fun StrokeColorPicker(
    strokeColor: CanvasColorOptions,
    onStrokeColorChange: (CanvasColorOptions) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Stroke",
            color = headingColor,
            style = headingStyle,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CanvasColorOptions.entries.forEach { color ->
                Box(
                    modifier = Modifier.size(28.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(color.foregroundColor)
                        .clickable(onClick = { onStrokeColorChange(color) })
                        .pointerHoverIcon(PointerIcon.Hand)
                        .thenIf(
                            condition = color == strokeColor,
                            modifier = Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = MaterialTheme.shapes.small
                            )
                        )
                )
            }
        }
    }
}