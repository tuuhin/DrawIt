package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import models.actions.CanvasDrawAction
import models.canvas.CanvasColorOptions
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption
import java.util.*

data class CanvasItemModel(
    val uuid: UUID = UUID.randomUUID(),
    val start: Offset,
    val end: Offset,
    val action: CanvasDrawAction,
    val strokeColor: CanvasColorOptions = CanvasColorOptions.BASE,
    val background: CanvasColorOptions = CanvasColorOptions.BASE,
    val pathEffect: PathEffectOptions = PathEffectOptions.SOLID,
    val strokeWidth: StrokeWidthOption = StrokeWidthOption.THIN,
    val alpha: Float = 1f,
) {

    constructor(
        start: Offset,
        end: Offset,
        type: CanvasDrawAction,
        style: CanvasDrawStyle,
    ) : this(
        start = start,
        end = end,
        action = type,
        strokeColor = style.strokeColor,
        background = style.background,
        pathEffect = style.pathEffect,
        strokeWidth = style.strokeOption,
        alpha = style.alpha
    )

    val boundingRect: Rect
        get() = Rect(start, end)
}
