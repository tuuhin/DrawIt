package presentation.style_picker

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
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
import models.canvas.*
import ui.DrawItAppTheme

@Composable
fun CanvasDrawStylePicker(
    style: CanvasDrawStyle,
    onStrokeColorChange: (CanvasColorOptions) -> Unit,
    onBackgroundColorChange: (CanvasColorOptions) -> Unit,
    onStrokeWidthChange: (StrokeWidthOption) -> Unit,
    onPathEffectChange: (PathEffectOptions) -> Unit,
    onBackgroundFillChange: (BackgroundFillOptions) -> Unit,
    onAlphaLevelChange: (Float) -> Unit,
    onRoundnessChange: (CornerRoundnessOption) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    elevation: Dp = 4.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    optionTextStyle: TextStyle = MaterialTheme.typography.titleSmall,
    optionHeadingColor: Color = MaterialTheme.colorScheme.onSurface,
) {

    val scrollState = rememberScrollState()
    val scrollAdapter = rememberScrollbarAdapter(scrollState)

    Surface(
        color = containerColor,
        contentColor = contentColorFor(containerColor),
        shape = shape,
        modifier = modifier.shadow(elevation = elevation, shape = shape)
    ) {
        Row(
            modifier = Modifier.sizeIn(maxHeight = 420.dp).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(scrollState).width(IntrinsicSize.Max),
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
                BackgroundFillPicker(
                    showPicker = style.background != CanvasColorOptions.BASE,
                    fillOption = style.backgroundFill,
                    onFillOptionChange = onBackgroundFillChange,
                    headingStyle = optionTextStyle,
                    headingColor = optionHeadingColor
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
            // scrollbar
            VerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight(),
                style = LocalScrollbarStyle.current.copy(
                    unhoverColor = MaterialTheme.colorScheme.inverseOnSurface,
                    hoverColor = MaterialTheme.colorScheme.surfaceVariant
                )
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
        onBackgroundFillChange = { onEvent(CanvasDrawStyleEvent.OnBackgroundFillChange(it)) },
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