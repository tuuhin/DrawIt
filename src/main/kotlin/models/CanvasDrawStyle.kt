package models

import models.canvas.CanvasColorOptions
import models.canvas.CornerRoundnessOption
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption

data class CanvasDrawStyle(
    val strokeColor: CanvasColorOptions = CanvasColorOptions.BASE,
    val background: CanvasColorOptions = CanvasColorOptions.BASE,
    val pathEffect: PathEffectOptions = PathEffectOptions.SOLID,
    val strokeOption: StrokeWidthOption = StrokeWidthOption.THIN,
    val roundness: CornerRoundnessOption = CornerRoundnessOption.NO_ROUND,
    val alpha: Float = 1f,
)
