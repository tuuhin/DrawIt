package event

import models.canvas.CanvasColorOptions
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption

sealed interface CanvasDrawStyleChangeEvent {

    data class OnStrokeOptionChange(val option: StrokeWidthOption) : CanvasDrawStyleChangeEvent
    data class OnStrokeColorChange(val colorOptions: CanvasColorOptions) : CanvasDrawStyleChangeEvent
    data class OnBackgroundColorChange(val colorOptions: CanvasColorOptions) : CanvasDrawStyleChangeEvent
    data class OnPathEffectChange(val pathEffectOptions: PathEffectOptions) : CanvasDrawStyleChangeEvent
    data class OnAlphaChange(val alpha: Float) : CanvasDrawStyleChangeEvent

}
