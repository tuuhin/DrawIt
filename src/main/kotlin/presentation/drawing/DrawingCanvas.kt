package presentation.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import event.CanvasPropertiesEvent
import mapper.backgroundColor
import mapper.foregroundColor
import mapper.toPathEffect
import mapper.width
import models.*
import ui.DrawItAppTheme

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun DrawingCanvas(
    state: ActionBarState,
    style: CanvasDrawStyle,
    propertiesState: CanvasPropertiesState,
    drawnObjects: CanvasDrawnObjects,
    onCreateNewObject: (CanvasItemModel) -> Unit,
    onCanvasPropertiesEvent: (CanvasPropertiesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var drawStartOffset by remember(state) { mutableStateOf(Offset.Zero) }
    var drawEndOffset by remember(state) { mutableStateOf(Offset.Zero) }
    val updatedStyle by rememberUpdatedState(style)

    val window = LocalWindowInfo.current

    val pointerIcon by remember(state.selectedDrawAction) {
        derivedStateOf {
            state.selectedDrawAction?.let { PointerIcon.Crosshair }
                ?: PointerIcon.Default
        }
    }

    Spacer(
        modifier = modifier
            .pointerHoverIcon(pointerIcon)
            .onPointerEvent(eventType = PointerEventType.Scroll) { event ->
                val change = event.changes.firstOrNull() ?: return@onPointerEvent

                val scrollDelta = change.scrollDelta
                val containerSize = window.containerSize

                val zoomAmount = event.calculateZoom()
                val panAmount = event.calculatePan()

                println("$scrollDelta $containerSize")
//                println("$zoomAmount $panAmount ${change.scrollDelta}")

//                onCanvasPropertiesEvent(CanvasPropertiesEvent.OnZoom(zoomAmount))
//                onCanvasPropertiesEvent(CanvasPropertiesEvent.OnPanCanvas(containerSize, scrollDelta))
            }
            .onDrag(
                matcher = PointerMatcher.mouse(PointerButton.Primary),
                onDragStart = { start ->
                    drawStartOffset = start
                    drawEndOffset = start
                },
                onDragEnd = {
                    // create and action
                    state.selectedDrawAction?.let { drawAction ->
                        val item = CanvasItemModel(
                            start = drawStartOffset,
                            end = drawEndOffset,
                            type = drawAction,
                            style = updatedStyle
                        )
                        // create a new object
                        onCreateNewObject(item)
                        // reset previous
                        drawEndOffset = Offset.Zero
                        drawStartOffset = Offset.Zero
                    }
                },
                onDragCancel = {
                    drawEndOffset = Offset.Zero
                    drawStartOffset = Offset.Zero

                },
                onDrag = { amount ->
                    drawEndOffset += amount
                },
            )
            .drawBehind {
                // this will draw the stuff
                withTransform(
                    transformBlock = {
                        scale(scale = propertiesState.scale)
                        translate(left = propertiesState.panedCanvas.x, top = propertiesState.panedCanvas.y)
                    },
                    drawBlock = {
                        drawnObjects.objects.fastForEach { drawObject ->
                            drawCanvasObjects(
                                boundingRect = drawObject.boundingRect,
                                action = drawObject.action,
                                style = Stroke(
                                    width = drawObject.strokeWidth.width.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = drawObject.pathEffect.toPathEffect(
                                        dottedInterval = 6.dp.toPx(),
                                        dashedInterval = 12.dp.toPx()
                                    )
                                ),
                                strokeColor = drawObject.strokeColor.foregroundColor,
                                fillColor = drawObject.background.backgroundColor,
                                alpha = drawObject.alpha
                            )
                        }
                    }
                )
            }.drawBehind {
                withTransform(
                    transformBlock = {
                        scale(propertiesState.scale)
                        translate(left = propertiesState.panedCanvas.x, top = propertiesState.panedCanvas.y)
                    },
                    drawBlock = {
                        state.selectedDrawAction?.let { drawAction ->
                            drawCanvasObjects(
                                boundingRect = Rect(drawStartOffset, drawEndOffset),
                                action = drawAction,
                                style = Stroke(
                                    width = style.strokeOption.width.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = style.pathEffect.toPathEffect(
                                        dottedInterval = 6.dp.toPx(),
                                        dashedInterval = 12.dp.toPx()
                                    )
                                ),
                                strokeColor = style.strokeColor.foregroundColor,
                                fillColor = style.background.backgroundColor,
                                alpha = style.alpha
                            )
                        }
                    },
                )
            },
    )
}


@Preview
@Composable
fun DrawingCanvasPreview() = DrawItAppTheme {
    DrawingCanvas(
        state = ActionBarState(),
        style = CanvasDrawStyle(),
        propertiesState = CanvasPropertiesState(),
        drawnObjects = CanvasDrawnObjects(),
        onCreateNewObject = {},
        onCanvasPropertiesEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}