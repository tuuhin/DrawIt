package presentation.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import mapper.backgroundColor
import mapper.foregroundColor
import mapper.toPathEffect
import mapper.width
import models.ActionBarState
import models.CanvasDrawStyle
import models.CanvasDrawnObjects
import models.CanvasItemModel
import ui.DrawItAppTheme

@Composable
fun DrawingCanvas(
    state: ActionBarState,
    style: CanvasDrawStyle,
    drawnObjects: CanvasDrawnObjects,
    onCreateNewObject: (CanvasItemModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var drawStartOffset by remember(state) { mutableStateOf(Offset.Zero) }
    var drawEndOffset by remember(state) { mutableStateOf(Offset.Zero) }
    val updatedStyle by rememberUpdatedState(style)

    val resetOffsets: () -> Unit = {
        drawEndOffset = Offset.Zero
        drawStartOffset = Offset.Zero
    }

    Spacer(
        modifier = modifier.pointerHoverIcon(PointerIcon.Crosshair)
            .pointerInput(state) {
                detectDragGestures(
                    onDragStart = { start ->
                        drawStartOffset = start
                        drawEndOffset = start
                    },
                    onDragEnd = {
                        // create and action
                        state.selectedDrawAction?.let { drawAction ->
                            val newCanvasObject = CanvasItemModel(
                                start = drawStartOffset,
                                end = drawEndOffset,
                                type = drawAction,
                                style = updatedStyle
                            )
                            // create a new object
                            onCreateNewObject(newCanvasObject)
                            // reset previous
                            resetOffsets()
                        }
                    },
                    onDragCancel = resetOffsets,
                    onDrag = { change, dragAmount ->
                        drawEndOffset += dragAmount
                        change.consume()
                    },
                )
            }
            .drawBehind {
                state.selectedDrawAction?.let { drawAction ->
                    drawCanvasObjects(
                        boundingRect = Rect(drawStartOffset, drawEndOffset),
                        action = drawAction,
                        style = Stroke(
                            width = style.strokeOption.width.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = style.pathEffect.toPathEffect()
                        ),
                        foregroundColor = style.strokeColor.foregroundColor,
                        backGroundColor = style.background.backgroundColor,
                        alpha = style.alpha
                    )
                }
            }
            .drawBehind {
                // this will draw the stuff
                drawnObjects.objects.forEach { drawObject ->
                    drawCanvasObjects(
                        boundingRect = drawObject.boundingRect,
                        action = drawObject.action,
                        style = Stroke(
                            width = drawObject.strokeWidth.width.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = drawObject.pathEffect.toPathEffect()
                        ),
                        foregroundColor = drawObject.strokeColor.foregroundColor,
                        backGroundColor = drawObject.background.backgroundColor,
                        alpha = drawObject.alpha
                    )
                }
            },
    )
}


@Preview
@Composable
fun DrawingCanvasPreview() = DrawItAppTheme {
    DrawingCanvas(
        state = ActionBarState(),
        style = CanvasDrawStyle(),
        drawnObjects = CanvasDrawnObjects(),
        onCreateNewObject = {},
        modifier = Modifier.fillMaxSize()
    )
}