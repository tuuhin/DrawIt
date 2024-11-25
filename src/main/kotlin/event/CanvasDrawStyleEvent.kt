package event

import models.canvas.*

sealed interface CanvasDrawStyleEvent {

    data class OnStrokeOptionChange(val option: StrokeWidthOption) : CanvasDrawStyleEvent
    data class OnStrokeColorChange(val colorOptions: CanvasColorOptions) : CanvasDrawStyleEvent
    data class OnBackgroundColorChange(val colorOptions: CanvasColorOptions) : CanvasDrawStyleEvent
    data class OnPathEffectChange(val pathEffectOptions: PathEffectOptions) : CanvasDrawStyleEvent
    data class OnAlphaChange(val alpha: Float) : CanvasDrawStyleEvent
    data class OnRoundnessChange(val roundness: CornerRoundnessOption) : CanvasDrawStyleEvent
    data class OnBackgroundFillChange(val fill: BackgroundFillOptions) : CanvasDrawStyleEvent

}
