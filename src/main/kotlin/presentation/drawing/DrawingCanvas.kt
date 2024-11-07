package presentation.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import event.CanvasPropertiesEvent
import mapper.backgroundColor
import mapper.foregroundColor
import mapper.toPathEffect
import mapper.width
import models.*
import ui.DrawItAppTheme
import kotlin.math.abs
import kotlin.math.sign

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
    var startOffset by remember(state) { mutableStateOf(Offset.Zero) }
    var endOffset by remember(state) { mutableStateOf(Offset.Zero) }
    val styleState by rememberUpdatedState(style)

    val pointerIcon = remember(state.selectedDrawAction) {
        state.selectedDrawAction?.let { PointerIcon.Crosshair }
            ?: PointerIcon.Default
    }

    Box(
        modifier = modifier
            .pointerHoverIcon(pointerIcon)
            .onKeyEvent { event ->
                val keyCtrlPlusComma =
                    (event.isCtrlPressed && event.key == Key.Comma && event.type == KeyEventType.KeyDown)
                if (keyCtrlPlusComma) {
                    onCanvasPropertiesEvent(CanvasPropertiesEvent.ToggleGridLinesVisibility)
                }
                keyCtrlPlusComma
            }
            .onPointerEvent(eventType = PointerEventType.Scroll) { event ->
                val change = event.changes.firstOrNull() ?: return@onPointerEvent
                val scrollDelta = change.scrollDelta * -1f
                // FIXME: Fix the problems regrading proper scroll

                // zoom events are made when scroll delta y component is 1 or -1
                if (abs(scrollDelta.y) == 1f) {
                    val zoomSign = scrollDelta.y.sign
                    onCanvasPropertiesEvent(CanvasPropertiesEvent.OnZoom(zoomSign))
                }
                onCanvasPropertiesEvent(CanvasPropertiesEvent.OnPanCanvas(scrollDelta.times(10.dp.toPx())))
            }
    ) {
        Spacer(
            modifier = Modifier.matchParentSize()
                .onDrag(
                    enabled = state.hasAction,
                    matcher = PointerMatcher.mouse(PointerButton.Primary),
                    onDragStart = { start ->
                        startOffset = start
                        endOffset = start
                    },
                    onDragEnd = {
                        // create and action
                        state.selectedDrawAction?.let { action ->
                            val item = CanvasItemModel(
                                start = startOffset - propertiesState.pannedScaledOffset,
                                end = endOffset - propertiesState.pannedScaledOffset,
                                action = action,
                                style = styleState,
                                scale = propertiesState.scale
                            )
                            // create a new object
                            onCreateNewObject(item)
                            // reset previous
                            endOffset = Offset.Zero
                            startOffset = Offset.Zero
                        }
                    },
                    onDragCancel = {
                        endOffset = Offset.Zero
                        startOffset = Offset.Zero
                    },
                    onDrag = { amount -> endOffset += amount },
                )
                .drawGraphLines(showGraph = propertiesState.showGraphLines)
                .drawBehind {
                    // this will draw the stuff
                    withTransform(
                        transformBlock = {
                            scale(propertiesState.scale)
                            translate(left = propertiesState.panedCanvas.x, top = propertiesState.panedCanvas.y)
                        },
                        drawBlock = {
                            drawnObjects.objects.fastForEach { drawObject ->
                                scale(1f / drawObject.scale) {
                                    drawCanvasObjects(
                                        boundingRect = drawObject.boundingRect,
                                        action = drawObject.action,
                                        stroke = Stroke(
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
                        }
                    )
                }.drawBehind {
                    state.selectedDrawAction?.let { drawAction ->
                        drawCanvasObjects(
                            boundingRect = Rect(startOffset, endOffset),
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
                            alpha = style.alpha
                        )
                    }
                },
        )
    }
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