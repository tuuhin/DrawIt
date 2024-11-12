package event

import models.canvas.CanvasColorOptions
import models.canvas.CornerRoundnessOption
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption

sealed interface CanvasDrawStyleEvent {

    data class OnStrokeOptionChange(val option: StrokeWidthOption) : CanvasDrawStyleEvent
    data class OnStrokeColorChange(val colorOptions: CanvasColorOptions) : CanvasDrawStyleEvent
    data class OnBackgroundColorChange(val colorOptions: CanvasColorOptions) : CanvasDrawStyleEvent
    data class OnPathEffectChange(val pathEffectOptions: PathEffectOptions) : CanvasDrawStyleEvent
    data class OnAlphaChange(val alpha: Float) : CanvasDrawStyleEvent
    data class OnRoundnessChange(val roundness: CornerRoundnessOption) : CanvasDrawStyleEvent

}
