package presentation.drawing.modifiers

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.util.fastForEach
import mapper.backgroundColor
import mapper.foregroundColor
import models.ActionBarState
import models.CanvasDrawnObjects
import models.CanvasPropertiesState
import presentation.drawing.draw_utils.drawCanvasObjects

fun Modifier.drawCanvasItems(
    state: ActionBarState,
    properties: CanvasPropertiesState,
    drawnObjects: CanvasDrawnObjects,
) = composed {

    val outlineColor = MaterialTheme.colorScheme.tertiary

    drawBehind {
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
                            strokeWidthOption = drawObject.strokeWidth,
                            pathEffectOptions = drawObject.pathEffect,
                            strokeColor = drawObject.strokeColor.foregroundColor,
                            fillColor = drawObject.background.backgroundColor,
                            alpha = drawObject.alpha,
                            isRounded = drawObject.isRounded,
                            fillMode = drawObject.style.backgroundFill,
                            hasBoundary = state.isSelectAction && drawObject.uuid == drawnObjects.selectedUUID,
                            boundaryColor = outlineColor,
                        )
                    }
                }
            },
        )
    }
}