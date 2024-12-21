package models

import models.canvas.*

data class CanvasDrawStyle(
    val strokeColor: CanvasColorOptions = CanvasColorOptions.BASE,
    val background: CanvasColorOptions = CanvasColorOptions.BASE,
    val pathEffect: PathEffectOptions = PathEffectOptions.SOLID,
    val strokeOption: StrokeWidthOption = StrokeWidthOption.THIN,
    val roundness: CornerRoundnessOption = CornerRoundnessOption.NO_ROUND,
    val backgroundFill: BackgroundFillOptions = BackgroundFillOptions.NONE,
    val alpha: Float = 1f,
) {
    val isRounded: Boolean
        get() = roundness == CornerRoundnessOption.ROUNDED

    fun onBackgroundColorChange(colorOptions: CanvasColorOptions): CanvasDrawStyle {
        return if (colorOptions == CanvasColorOptions.BASE)
            copy(background = colorOptions, backgroundFill = BackgroundFillOptions.NONE)
        else if (backgroundFill == BackgroundFillOptions.NONE)
            copy(background = colorOptions, backgroundFill = BackgroundFillOptions.SOLID)
        else copy(background = colorOptions, backgroundFill = backgroundFill)
    }

}
