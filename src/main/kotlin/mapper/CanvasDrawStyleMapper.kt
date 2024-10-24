package mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import models.canvas.CanvasColorOptions
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption

val CanvasColorOptions.foregroundColor: Color
    get() = when (this) {
        CanvasColorOptions.BASE -> Color.White
        CanvasColorOptions.RED -> Color(0xfffcafa5)
        CanvasColorOptions.GREEN -> Color(0xff86efac)
        CanvasColorOptions.BLUE -> Color(0xff93c5fd)
        CanvasColorOptions.YELLOW -> Color(0xfffde047)
    }

val CanvasColorOptions.backgroundColor: Color
    get() = when (this) {
        CanvasColorOptions.BASE -> Color.Transparent
        CanvasColorOptions.RED -> Color(0xffdc2626)
        CanvasColorOptions.GREEN -> Color(0xff16a34a)
        CanvasColorOptions.BLUE -> Color(0xff2563eb)
        CanvasColorOptions.YELLOW -> Color(0xffca8a04)
    }

fun PathEffectOptions.toPathEffect(
    dottedIntervals: FloatArray = floatArrayOf(5f, 5f),
    dashedIntervals: FloatArray = floatArrayOf(10f, 10f),
): PathEffect? {
    return when (this) {
        PathEffectOptions.SOLID -> null
        PathEffectOptions.DASHED -> PathEffect.dashPathEffect(dashedIntervals)
        PathEffectOptions.DOTTED -> PathEffect.dashPathEffect(dottedIntervals)
    }
}

val StrokeWidthOption.width: Dp
    get() = when (this) {
        StrokeWidthOption.THIN -> 2.dp
        StrokeWidthOption.BOLD -> 4.dp
        StrokeWidthOption.EXTRA_BOLD -> 6.dp
    }