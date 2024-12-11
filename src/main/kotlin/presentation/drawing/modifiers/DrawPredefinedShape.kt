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
import androidx.compose.ui.input.pointer.PointerButton
import mapper.backgroundColor
import mapper.foregroundColor
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
                // perform switch its better place to switch the co-ordinates
                val startPoint = Offset(
                    x = if (startOffset.x <= endOffset.x) startOffset.x else endOffset.x,
                    y = if (startOffset.y <= endOffset.y) startOffset.y else endOffset.y
                ) - propertiesState.panedScaledOffset

                val endPoint = Offset(
                    x = if (startOffset.x <= endOffset.x) endOffset.x else startOffset.x,
                    y = if (startOffset.y <= endOffset.y) endOffset.y else startOffset.y
                ) - propertiesState.panedScaledOffset

                // add the item
                val item = CanvasItemModel(
                    start = startPoint,
                    end = endPoint,
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
                strokeWidthOption = style.strokeOption,
                pathEffectOptions = style.pathEffect,
                strokeColor = style.strokeColor.foregroundColor,
                fillColor = style.background.backgroundColor,
                fillMode = style.backgroundFill,
                alpha = style.alpha,
                isRounded = style.isRounded,
            )
        }
    }
}
