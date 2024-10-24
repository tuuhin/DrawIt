package mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import com.eva.draw_it.drawit.generated.resources.*
import models.actions.ActionBarActions
import models.actions.CanvasDrawAction
import models.actions.CanvasUtilAction
import org.jetbrains.compose.resources.painterResource

val ActionBarActions.actionPainter: Painter
    @Composable
    get() = when (this) {
        CanvasDrawAction.ACTION_ARROW -> painterResource(Res.drawable.ic_arrow)
        CanvasDrawAction.ACTION_DIAMOND -> painterResource(Res.drawable.ic_rhombus)
        CanvasDrawAction.ACTION_DRAW -> painterResource(Res.drawable.ic_pen)
        CanvasDrawAction.ACTION_ELLIPSE -> painterResource(Res.drawable.ic_circle)
        CanvasDrawAction.ACTION_ERASER -> painterResource(Res.drawable.ic_eraser)
        CanvasDrawAction.ACTION_LINE -> painterResource(Res.drawable.ic_dash)
        CanvasDrawAction.ACTION_RECT -> painterResource(Res.drawable.ic_square)
        CanvasDrawAction.ACTION_SELECT -> painterResource(Res.drawable.ic_pointer)
        CanvasDrawAction.ACTION_TEXT -> painterResource(Res.drawable.ic_text)
        CanvasUtilAction.ACTION_LOCK_CANVAS -> painterResource(Res.drawable.ic_lock)
        CanvasUtilAction.ACTION_HAND -> painterResource(Res.drawable.ic_hand)
    }


val ActionBarActions.keyboardShortcuts: Collection<Key>
    get() = when (this) {
        CanvasDrawAction.ACTION_ARROW -> setOf(Key.A, Key.Five)
        CanvasDrawAction.ACTION_DIAMOND -> setOf(Key.D, Key.Three)
        CanvasDrawAction.ACTION_DRAW -> setOf(Key.D, Key.Seven)
        CanvasDrawAction.ACTION_ELLIPSE -> setOf(Key.C, Key.Four)
        CanvasDrawAction.ACTION_ERASER -> setOf(Key.E, Key.Zero)
        CanvasDrawAction.ACTION_LINE -> setOf(Key.L, Key.Six)
        CanvasDrawAction.ACTION_RECT -> setOf(Key.R, Key.Two)
        CanvasDrawAction.ACTION_SELECT -> setOf(Key.V, Key.One)
        CanvasDrawAction.ACTION_TEXT -> setOf(Key.Eight, Key.T)
        CanvasUtilAction.ACTION_LOCK_CANVAS -> setOf(Key.Q)
        CanvasUtilAction.ACTION_HAND -> setOf(Key.H)
    }

val CanvasDrawAction.overlayText: String
    get() = when (this) {
        CanvasDrawAction.ACTION_ERASER -> "0"
        CanvasDrawAction.ACTION_TEXT -> "8"
        CanvasDrawAction.ACTION_DRAW -> "7"
        CanvasDrawAction.ACTION_LINE -> "6"
        CanvasDrawAction.ACTION_ARROW -> "5"
        CanvasDrawAction.ACTION_ELLIPSE -> "4"
        CanvasDrawAction.ACTION_DIAMOND -> "3"
        CanvasDrawAction.ACTION_RECT -> "2"
        CanvasDrawAction.ACTION_SELECT -> "1"
    }

val ActionBarActions.tooltipText: String
    get() = when (this) {
        CanvasUtilAction.ACTION_LOCK_CANVAS -> "Keep Selected tool active for drawing - Q"
        CanvasUtilAction.ACTION_HAND -> "Hand - H"
        CanvasDrawAction.ACTION_ERASER -> "Eraser 0"
        CanvasDrawAction.ACTION_TEXT -> "Text- T or 8"
        CanvasDrawAction.ACTION_DRAW -> "Draw - D or 7"
        CanvasDrawAction.ACTION_LINE -> "Line - L or 6"
        CanvasDrawAction.ACTION_ARROW -> "Arrow - A or 5"
        CanvasDrawAction.ACTION_ELLIPSE -> "Ellipse -  C or 4"
        CanvasDrawAction.ACTION_DIAMOND -> "Diamond- D or 3"
        CanvasDrawAction.ACTION_RECT -> "Rectangle - R or 2"
        CanvasDrawAction.ACTION_SELECT -> "Selection - V or 1"
    }
