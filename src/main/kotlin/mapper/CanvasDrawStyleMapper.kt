package mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.ic_edge_rounded
import com.eva.draw_it.drawit.generated.resources.ic_edge_straight
import models.canvas.CanvasColorOptions
import models.canvas.CornerRoundnessOption
import models.canvas.PathEffectOptions
import models.canvas.StrokeWidthOption
import org.jetbrains.compose.resources.painterResource

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
    dottedInterval: Float = 5f,
    dashedInterval: Float = 10f,
): PathEffect? {
    return when (this) {
        PathEffectOptions.SOLID -> null
        PathEffectOptions.DASHED -> PathEffect.dashPathEffect(intervals = List(2) { dashedInterval }.toFloatArray())
        PathEffectOptions.DOTTED -> PathEffect.dashPathEffect(intervals = List(2) { dottedInterval }.toFloatArray())
    }
}

val CornerRoundnessOption.painterRes: Painter
    @Composable
    get() = when (this) {
        CornerRoundnessOption.NO_ROUND -> painterResource(Res.drawable.ic_edge_straight)
        CornerRoundnessOption.ROUNDED -> painterResource(Res.drawable.ic_edge_rounded)
    }

val StrokeWidthOption.width: Dp
    get() = when (this) {
        StrokeWidthOption.THIN -> 2.dp
        StrokeWidthOption.BOLD -> 4.dp
        StrokeWidthOption.EXTRA_BOLD -> 6.dp
    }