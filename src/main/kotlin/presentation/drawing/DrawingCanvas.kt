package presentation.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import event.CanvasItemEvent
import event.CanvasPropertiesEvent
import mapper.backgroundColor
import mapper.foregroundColor
import mapper.toPathEffect
import mapper.width
import models.*
import ui.DrawItAppTheme
import kotlin.math.abs
import kotlin.math.sign


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawingCanvas(
    state: ActionBarState,
    style: CanvasDrawStyle,
    properties: CanvasPropertiesState,
    drawnObjects: CanvasDrawnObjects,
    onAddItem: (CanvasItemModel) -> Unit,
    onSelectItem: (CanvasItemModel) -> Unit,
    onZoomCanvas: (Float) -> Unit,
    onPanCanvas: (Offset) -> Unit,
    modifier: Modifier = Modifier,
    onDeSelectItem: () -> Unit = {},
) {
    var startOffset by remember(state) { mutableStateOf(Offset.Zero) }
    var endOffset by remember(state) { mutableStateOf(Offset.Zero) }
    val styleState by rememberUpdatedState(style)

    val pointerIcon = remember(state.action, drawnObjects.selectedObject) {
        state.action?.let { PointerIcon.Crosshair }
            ?: PointerIcon.Default
    }

    val outlineColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .pointerHoverIcon(icon = pointerIcon)
            .doubleScrollOrZoom(onPan = onPanCanvas, onZoom = onZoomCanvas)
            .observeMouseMovements(
                enabled = state.isSelectAction,
                items = drawnObjects,
                onSelectObject = onSelectItem,
                onDeselectObject = onDeSelectItem
            )
    ) {
        Spacer(
            modifier = Modifier.matchParentSize()
                .onDrag(
                    enabled = state.isActionDraw,
                    matcher = PointerMatcher.mouse(PointerButton.Primary),
                    onDragStart = { start ->
                        startOffset = start
                        endOffset = start
                    },
                    onDragEnd = {
                        // create and action
                        state.selectedDrawAction?.let { action ->
                            val item = CanvasItemModel(
                                start = startOffset - properties.pannedScaledOffset,
                                end = endOffset - properties.pannedScaledOffset,
                                action = action,
                                style = styleState,
                                scale = properties.scale
                            )
                            // create a new object
                            onAddItem(item)
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
                .drawGraphLines(showGraph = properties.showGraphLines)
                .drawBehind {
                    // this will draw the stuff
                    withTransform(
                        transformBlock = {
                            scale(scale = properties.scale)
                            translate(left = properties.panedCanvas.x, top = properties.panedCanvas.y)
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
                                        alpha = drawObject.alpha,
                                        isRounded = drawObject.isRounded,
                                        hasBoundary = state.isSelectAction && drawnObjects.selectedObject == drawObject,
                                        boundaryColor = outlineColor
                                    )
                                }
                            }
                        },
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
                            alpha = style.alpha,
                            isRounded = style.isRounded,
                        )
                    }
                },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.observeMouseMovements(
    items: CanvasDrawnObjects,
    onSelectObject: (CanvasItemModel) -> Unit,
    onDeselectObject: () -> Unit = {},
    enabled: Boolean = true,
) = composed {
    if (!enabled) return@composed Modifier

    onPointerEvent(eventType = PointerEventType.Press) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent
        // matching object
        items.objects.find { it.boundingRect.contains(change.position) }?.let { item ->
            if (items.selectedObject == item) onDeselectObject()
            else onSelectObject(item)
        } ?: onDeselectObject()
    }
}


@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.doubleScrollOrZoom(
    onPan: (Offset) -> Unit,
    onZoom: (Float) -> Unit,
    speed: Float = 10f,
) = then(
    Modifier.onPointerEvent(eventType = PointerEventType.Scroll) { event ->
        val change = event.changes.firstOrNull() ?: return@onPointerEvent
        val scrollDelta = change.scrollDelta * -1f
        // FIXME: Fix the problems regrading proper scroll

        // zoom events are made when scroll delta y component is 1 or -1
        if (abs(scrollDelta.y) == 1f) {
            val zoomSign = scrollDelta.y.sign
            onZoom(zoomSign)
        }
        onPan(scrollDelta.times(speed))
    }
)

@Composable
fun DrawingCanvas(
    state: ActionBarState,
    style: CanvasDrawStyle,
    propertiesState: CanvasPropertiesState,
    drawnObjects: CanvasDrawnObjects,
    onCanvasPropertiesEvent: (CanvasPropertiesEvent) -> Unit,
    onInteractionEvent: (CanvasItemEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    DrawingCanvas(
        state = state,
        style = style,
        properties = propertiesState,
        drawnObjects = drawnObjects,
        onAddItem = { onInteractionEvent(CanvasItemEvent.OnAddNewCanvasItem(it)) },
        onSelectItem = { onInteractionEvent(CanvasItemEvent.OnSelectCanvasItem(it)) },
        onDeSelectItem = { onInteractionEvent(CanvasItemEvent.OnDeSelectCanvasItem) },
        onZoomCanvas = { onCanvasPropertiesEvent(CanvasPropertiesEvent.OnZoom(it)) },
        onPanCanvas = { onCanvasPropertiesEvent(CanvasPropertiesEvent.OnPanCanvas(it)) },
        modifier = modifier,
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
        onInteractionEvent = {},
        onCanvasPropertiesEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}