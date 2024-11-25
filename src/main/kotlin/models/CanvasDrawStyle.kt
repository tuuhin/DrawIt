package models

import models.canvas.*

data class CanvasDrawStyle(
    val strokeColor: CanvasColorOptions = CanvasColorOptions.BASE,
    val background: CanvasColorOptions = CanvasColorOptions.BASE,
    val pathEffect: PathEffectOptions = PathEffectOptions.SOLID,
    val strokeOption: StrokeWidthOption = StrokeWidthOption.THIN,
    val roundness: CornerRoundnessOption = CornerRoundnessOption.NO_ROUND,
    val backgroundFill: BackgroundFillOptions? = null,
    val alpha: Float = 1f,
) {
    val isRounded: Boolean
        get() = roundness == CornerRoundnessOption.ROUNDED
}
