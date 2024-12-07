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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import event.CanvasItemEvent
import event.CanvasPropertiesEvent
import mapper.backgroundColor
import mapper.foregroundColor
import mapper.toPathEffect
import mapper.width
import models.*
import presentation.drawing.utils.doubleScrollOrZoom
import presentation.drawing.utils.drawGraphLines
import presentation.drawing.utils.observeItemInteractions
import ui.DrawItAppTheme
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawingCanvas(
    state: ActionBarState,
    style: CanvasDrawStyle,
    properties: CanvasPropertiesState,
    drawnObjects: CanvasDrawnObjects,
    onAddItem: (CanvasItemModel) -> Unit,
    onSelectItem: (UUID) -> Unit,
    onPanSelectedItem: (item: UUID, pan: Offset) -> Unit,
    onResizeSelectedItem: (item: UUID, rect: Rect) -> Unit,
    onRotateSelectedItem: (item: UUID, degree: Float) -> Unit,
    onZoomCanvas: (Float) -> Unit,
    onPanCanvas: (Offset) -> Unit,
    modifier: Modifier = Modifier,
    onDeSelectItem: () -> Unit = {},
) {
    var startOffset by remember(state) { mutableStateOf(Offset.Zero) }
    var endOffset by remember(state) { mutableStateOf(Offset.Zero) }
    val styleState by rememberUpdatedState(style)

    val pointerIcon = remember(state.action) {
        state.action?.let { PointerIcon.Crosshair }
            ?: PointerIcon.Default
    }

    val outlineColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .pointerHoverIcon(icon = pointerIcon)
            .doubleScrollOrZoom(onPan = onPanCanvas, onZoom = onZoomCanvas)
            .observeItemInteractions(
                enabled = state.isSelectAction,
                items = drawnObjects,
                properties = properties,
                onSelectObject = onSelectItem,
                onPanCanvasItem = onPanSelectedItem,
                onDeselectObject = onDeSelectItem,
                onResizeCanvasItem = onResizeSelectedItem,
                onRotateItem = onRotateSelectedItem
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
                                start = startOffset - properties.panedScaledOffset,
                                end = endOffset - properties.panedScaledOffset,
                                action = action,
                                style = styleState,
                                scale = properties.canvasScale
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
                .drawGraphLines(showGraph = true)
                .drawBehind {
                    // this will draw the stuff
                    withTransform(
                        transformBlock = {
                            scale(scale = properties.canvasScale, pivot = center)
                            translate(left = properties.panedCanvas.x, top = properties.panedCanvas.y)
                        },
                        drawBlock = {
                            drawnObjects.canvasItems.fastForEach { drawObject ->
                                withTransform(
                                    transformBlock = {
                                        scale(
                                            scale = drawObject.reciprocalScale,
                                            pivot = drawObject.boundingRect.center
                                        )
                                        rotate(
                                            degrees = drawObject.rotateInDegree,
                                            pivot = drawObject.boundingRect.center
                                        )
                                    },
                                ) {
                                    drawCanvasObjects(
                                        boundingRect = drawObject.boundingRect,
                                        properties = properties,
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
                                        hasBoundary = state.isSelectAction && drawObject.uuid == drawnObjects.selectedUUID,
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
                            alpha = style.alpha,
                            isRounded = style.isRounded,
                        )
                    }
                },
        )
    }
}

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
        onPanSelectedItem = { item, pan -> onInteractionEvent(CanvasItemEvent.OnMoveSelectedItem(item, pan)) },
        onResizeSelectedItem = { item, rect -> onInteractionEvent(CanvasItemEvent.OnResizeSelectedItem(item, rect)) },
        onRotateSelectedItem = { item, degree ->
            onInteractionEvent(CanvasItemEvent.OnRotateSelectedItem(item, degree))
        },
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