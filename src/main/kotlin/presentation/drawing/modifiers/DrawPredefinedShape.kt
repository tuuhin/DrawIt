package presentation.drawing.modifiers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.unit.dp
import mapper.backgroundColor
import mapper.foregroundColor
import mapper.toPathEffect
import mapper.width
import models.ActionBarState
import models.CanvasDrawStyle
import models.CanvasItemModel
import models.CanvasPropertiesState
import presentation.drawing.draw_utils.drawCanvasObjects


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.onDrawViaActionBarAction(
    actionBarState: ActionBarState,
    style: CanvasDrawStyle,
    properties: CanvasPropertiesState,
    enabled: Boolean = true,
    onAddItem: (CanvasItemModel) -> Unit,
) = composed {

    var startOffset by remember(actionBarState) { mutableStateOf(Offset.Zero) }
    var endOffset by remember(actionBarState) { mutableStateOf(Offset.Zero) }

    val styleState by rememberUpdatedState(style)
    val propertiesState by rememberUpdatedState(properties)

    onDrag(
        enabled = enabled && actionBarState.isSelectedActionPredefinedShape,
        matcher = PointerMatcher.mouse(PointerButton.Primary),
        onDragStart = { start ->
            startOffset = start
            endOffset = start
        },
        onDragEnd = {
            // create and action
            actionBarState.selectedDrawAction?.let { action ->
                val item = CanvasItemModel(
                    start = startOffset - propertiesState.panedScaledOffset,
                    end = endOffset - propertiesState.panedScaledOffset,
                    action = action,
                    style = styleState,
                    scale = propertiesState.canvasScale
                )
                // create a new object
                onAddItem(item)
                // reset previous
                endOffset = Offset.Zero
                startOffset = Offset.Zero
            }
        },
        onDragCancel = {
            // rest the points
            endOffset = Offset.Zero
            startOffset = Offset.Zero
        },
        onDrag = { amount -> endOffset += amount },
    ).drawBehind {
        actionBarState.selectedDrawAction?.let { drawAction ->
            drawCanvasObjects(
                boundingRect = Rect(startOffset, endOffset),
                properties = properties,
                action = drawAction,
                stroke = Stroke(
                    width = style.strokeOption.width.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = style.pathEffect.toPathEffect(
                        dottedInterval = 6.dp.toPx(),
                        dashedInterval = 12.dp.toPx()
                    )
                ),
                strokeColor = style.strokeColor.foregroundColor,
                fillColor = style.background.backgroundColor,
                fillMode = style.backgroundFill,
                alpha = style.alpha,
                isRounded = style.isRounded,
            )
        }
    }
}
