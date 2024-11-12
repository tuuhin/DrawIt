package presentation.style_picker

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import event.CanvasDrawStyleEvent
import models.CanvasDrawStyle
import models.canvas.CanvasColorOptions
import models.canvas.CornerRoundnessOption
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption
import ui.DrawItAppTheme

@Composable
fun CanvasDrawStylePicker(
    style: CanvasDrawStyle,
    onStrokeColorChange: (CanvasColorOptions) -> Unit,
    onBackgroundColorChange: (CanvasColorOptions) -> Unit,
    onStrokeWidthChange: (StrokeWidthOption) -> Unit,
    onPathEffectChange: (PathEffectOptions) -> Unit,
    onAlphaLevelChange: (Float) -> Unit,
    onRoundnessChange: (CornerRoundnessOption) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    elevation: Dp = 4.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    optionTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    optionHeadingColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        color = containerColor,
        contentColor = contentColorFor(containerColor),
        shape = shape,
        modifier = modifier.shadow(elevation = elevation, shape = shape)
            .sizeIn(maxHeight = 720.dp)
            .width(IntrinsicSize.Max)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StrokeColorPicker(
                strokeColor = style.strokeColor,
                onStrokeColorChange = onStrokeColorChange,
                headingStyle = optionTextStyle,
                headingColor = optionHeadingColor,
            )
            BackgroundColorPicker(
                backGroundColor = style.background,
                onBackgroundColorChange = onBackgroundColorChange,
                headingStyle = optionTextStyle,
                headingColor = optionHeadingColor,
            )
            PathEffectPicker(
                pathEffect = style.pathEffect,
                onPathEffectChange = onPathEffectChange,
                headingStyle = optionTextStyle,
                headingColor = optionHeadingColor
            )
            CornerRadiusPicker(
                option = style.roundness,
                onOptionChange = onRoundnessChange,
                headingStyle = optionTextStyle,
                headingColor = optionHeadingColor,
            )
            StrokeWidthPicker(
                canvasStrokeOption = style.strokeOption,
                onStrokeWidthChange = onStrokeWidthChange,
                headingStyle = optionTextStyle,
                headingColor = optionHeadingColor,
            )
            OpacityPicker(
                level = style.alpha,
                onLevelChange = onAlphaLevelChange,
                headingStyle = optionTextStyle,
                headingColor = optionHeadingColor,
            )
        }
    }
}

@Composable
fun CanvasDrawStylePicker(
    style: CanvasDrawStyle,
    onEvent: (CanvasDrawStyleEvent) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    elevation: Dp = 2.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    optionTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    optionHeadingColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    CanvasDrawStylePicker(
        style = style,
        onStrokeWidthChange = { onEvent(CanvasDrawStyleEvent.OnStrokeOptionChange(it)) },
        onPathEffectChange = { onEvent(CanvasDrawStyleEvent.OnPathEffectChange(it)) },
        onStrokeColorChange = { onEvent(CanvasDrawStyleEvent.OnStrokeColorChange(it)) },
        onBackgroundColorChange = { onEvent(CanvasDrawStyleEvent.OnBackgroundColorChange(it)) },
        onAlphaLevelChange = { onEvent(CanvasDrawStyleEvent.OnAlphaChange(it)) },
        onRoundnessChange = { onEvent(CanvasDrawStyleEvent.OnRoundnessChange(it)) },
        modifier = modifier,
        containerColor = containerColor,
        elevation = elevation,
        shape = shape,
        optionTextStyle = optionTextStyle,
        optionHeadingColor = optionHeadingColor
    )
}


@Preview
@Composable
fun CanvasDrawStylePickerPreview() = DrawItAppTheme {
    CanvasDrawStylePicker(
        style = CanvasDrawStyle(alpha = .5f),
        onEvent = {},
    )
}