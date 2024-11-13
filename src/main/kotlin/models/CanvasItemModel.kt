package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import models.actions.CanvasDrawAction
import models.canvas.CanvasColorOptions
import models.canvas.CornerRoundnessOption
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption
import java.util.*

data class CanvasItemModel(
    val uuid: UUID = UUID.randomUUID(),
    val start: Offset,
    val end: Offset,
    val action: CanvasDrawAction,
    val style: CanvasDrawStyle = CanvasDrawStyle(),
    val scale: Float = 1f,
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

    infix fun isSameAs(other: CanvasItemModel?) = other?.uuid == uuid
}
