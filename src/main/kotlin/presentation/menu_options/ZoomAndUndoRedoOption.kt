package presentation.menu_options

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.*
import event.CanvasPropertiesEvent
import models.CanvasPropertiesState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.DrawItAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomAndUndoRedoOption(
    scale: Float,
    isUndoEnabled: Boolean,
    isRedoEnabled: Boolean,
    onResetZoom: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val zoomPercentage by remember(scale) {
        derivedStateOf {
            val value = scale * 100f
            String.format("%.1f", value) + "%"
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = containerColor,
            tonalElevation = elevation,
            shape = shape,
            modifier = Modifier.clip(shape),
        ) {
            Row(
                modifier = Modifier.padding(6.dp).height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BoxButton(
                    onClick = onZoomIn,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_plus),
                        contentDescription = stringResource(Res.string.canvas_action_zoom_in),
                    )
                }
                VerticalDivider(modifier = Modifier.padding(start = 4.dp))
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(text = stringResource(Res.string.canvas_reset_zoom))
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.sizeIn(minWidth = 64.dp)
                            .clickable(onClick = onResetZoom, role = Role.Button)
                    ) {
                        Text(
                            text = zoomPercentage,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                VerticalDivider(modifier = Modifier.padding(end = 4.dp))
                BoxButton(
                    onClick = onZoomOut,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_minus),
                        contentDescription = stringResource(Res.string.canvas_action_zoom_out),
                    )
                }
            }
        }

        Surface(
            color = containerColor,
            tonalElevation = elevation,
            shape = shape,
            modifier = Modifier.clip(shape)
        ) {
            Row(
                modifier = Modifier.padding(6.dp).height(IntrinsicSize.Max),
            ) {
                BoxButton(
                    onClick = onUndo,
                    enabled = isUndoEnabled,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_undo),
                        contentDescription = stringResource(Res.string.canvas_action_undo),
                    )
                }
                VerticalDivider(modifier = Modifier.padding(horizontal = 4.dp))
                BoxButton(
                    onClick = onRedo,
                    enabled = isRedoEnabled,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_redo),
                        contentDescription = stringResource(Res.string.canvas_action_redo),
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(4.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button }.aspectRatio(1f),
        enabled = enabled,
        shape = shape,
        color = if (enabled) colors.containerColor else colors.disabledContainerColor,
        contentColor = if (enabled) colors.contentColor else colors.disabledContentColor,
        border = border,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier.defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
fun ZoomAndUndoRedoOption(
    properties: CanvasPropertiesState,
    onEvent: (CanvasPropertiesEvent) -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    shape: Shape = MaterialTheme.shapes.small,
) {
    ZoomAndUndoRedoOption(
        scale = properties.canvasScale,
        isRedoEnabled = properties.redoEnabled,
        isUndoEnabled = properties.undoEnabled,
        onRedo = { onEvent(CanvasPropertiesEvent.OnRedo) },
        onUndo = { onEvent(CanvasPropertiesEvent.OnUndo) },
        onZoomIn = { onEvent(CanvasPropertiesEvent.OnIncrementZoom) },
        onZoomOut = { onEvent(CanvasPropertiesEvent.OnDecrementZoom) },
        onResetZoom = { onEvent(CanvasPropertiesEvent.OnResetZoom) },
        shape = shape,
        elevation = elevation,
        containerColor = containerColor,
        modifier = modifier,
    )
}


@Preview
@Composable
private fun ZoomAndUndoRedoOptionPreview() = DrawItAppTheme {
    ZoomAndUndoRedoOption(
        properties = CanvasPropertiesState(),
        onEvent = {},
    )
}