package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import models.actions.CanvasDrawAction
import models.canvas.CanvasColorOptions
import models.canvas.CornerRoundnessOption
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption
import java.util.*
import kotlin.math.PI

data class CanvasItemModel(
    val uuid: UUID = UUID.randomUUID(),
    val start: Offset,
    val end: Offset,
    val action: CanvasDrawAction,
    val style: CanvasDrawStyle = CanvasDrawStyle(),
    val scale: Float = 1f,
    val rotateInRadians: Float = 0f,
) {
    val strokeColor: CanvasColorOptions
        get() = style.strokeColor

    val background: CanvasColorOptions
        get() = style.background

    val pathEffect: PathEffectOptions
        get() = style.pathEffect

    val strokeWidth: StrokeWidthOption
        get() = style.strokeOption

    val alpha: Float
        get() = style.alpha

    val isRounded: Boolean
        get() = style.roundness == CornerRoundnessOption.ROUNDED

    val boundingRect: Rect
        get() = Rect(start, end)

    val reciprocalScale: Float
        get() = 1 / scale


    val rotateInDegree: Float
        get() {
            val normalize = ((rotateInRadians % (2 * PI)) + 2 * PI) % (2 * PI)
            return Math.toDegrees(normalize).toFloat()
        }


    companion object {
        // multiplier from top center position
        val Density.AXLE_POSITION_OFFSET: Float
            get() = 20.dp.toPx()
    }
}
