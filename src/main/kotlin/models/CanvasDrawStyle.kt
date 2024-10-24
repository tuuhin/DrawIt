package models

import models.canvas.CanvasColorOptions
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption

data class CanvasDrawStyle(
    val strokeColor: CanvasColorOptions = CanvasColorOptions.BASE,
    val background: CanvasColorOptions = CanvasColorOptions.BASE,
    val pathEffect: PathEffectOptions = PathEffectOptions.SOLID,
    val strokeOption: StrokeWidthOption = StrokeWidthOption.THIN,
    val alpha: Float = 1f,
)
