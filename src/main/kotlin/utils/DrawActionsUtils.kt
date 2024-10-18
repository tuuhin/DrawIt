package utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import com.eva.draw_it.drawit.generated.resources.*
import models.ActionBarActions
import org.jetbrains.compose.resources.painterResource

val ActionBarActions.actionPainter: Painter
    @Composable
    get() = when (this) {
        ActionBarActions.ACTION_ARROW -> painterResource(Res.drawable.ic_arrow)
        ActionBarActions.ACTION_DIAMOND -> painterResource(Res.drawable.ic_rhombus)
        ActionBarActions.ACTION_DRAW -> painterResource(Res.drawable.ic_pen)
        ActionBarActions.ACTION_ELLIPSE -> painterResource(Res.drawable.ic_circle)
        ActionBarActions.ACTION_ERASER -> painterResource(Res.drawable.ic_eraser)
        ActionBarActions.ACTION_IMAGE -> painterResource(Res.drawable.ic_image)
        ActionBarActions.ACTION_LINE -> painterResource(Res.drawable.ic_dash)
        ActionBarActions.ACTION_RECT -> painterResource(Res.drawable.ic_square)
        ActionBarActions.ACTION_SELECT -> painterResource(Res.drawable.ic_pointer)
        ActionBarActions.ACTION_TEXT -> painterResource(Res.drawable.ic_text)
        ActionBarActions.ACTION_LOCK_CANVAS -> painterResource(Res.drawable.ic_lock)
        ActionBarActions.ACTION_HAND -> painterResource(Res.drawable.ic_hand)
    }

val ActionBarActions.keyboardShortcuts: Collection<Key>
    get() = when (this) {
        ActionBarActions.ACTION_ARROW -> setOf(Key.A, Key.Five)
        ActionBarActions.ACTION_DIAMOND -> setOf(Key.D, Key.Three)
        ActionBarActions.ACTION_DRAW -> setOf(Key.D, Key.Seven)
        ActionBarActions.ACTION_ELLIPSE -> setOf(Key.C, Key.Four)
        ActionBarActions.ACTION_ERASER -> setOf(Key.E, Key.Zero)
        ActionBarActions.ACTION_IMAGE -> setOf(Key.Nine)
        ActionBarActions.ACTION_LINE -> setOf(Key.L, Key.Six)
        ActionBarActions.ACTION_RECT -> setOf(Key.R, Key.Two)
        ActionBarActions.ACTION_SELECT -> setOf(Key.V, Key.One)
        ActionBarActions.ACTION_TEXT -> setOf(Key.Eight, Key.T)
        ActionBarActions.ACTION_LOCK_CANVAS -> setOf(Key.Q)
        ActionBarActions.ACTION_HAND -> setOf(Key.H)
    }

val ActionBarActions.overlayText: String?
    get() = when (this) {
        ActionBarActions.ACTION_ERASER -> "0"
        ActionBarActions.ACTION_IMAGE -> "9"
        ActionBarActions.ACTION_TEXT -> "8"
        ActionBarActions.ACTION_DRAW -> "7"
        ActionBarActions.ACTION_LINE -> "6"
        ActionBarActions.ACTION_ARROW -> "5"
        ActionBarActions.ACTION_ELLIPSE -> "4"
        ActionBarActions.ACTION_DIAMOND -> "3"
        ActionBarActions.ACTION_RECT -> "2"
        ActionBarActions.ACTION_SELECT -> "1"
        else -> null
    }

val ActionBarActions.tooltipText: String
    get() = when (this) {
        ActionBarActions.ACTION_LOCK_CANVAS -> "Keep Selected tool active for drawing - Q"
        ActionBarActions.ACTION_HAND -> "Hand - H"
        ActionBarActions.ACTION_ERASER -> "Eraser 0"
        ActionBarActions.ACTION_IMAGE -> "Image  9"
        ActionBarActions.ACTION_TEXT -> "Text- T or 8"
        ActionBarActions.ACTION_DRAW -> "Draw - D or 7"
        ActionBarActions.ACTION_LINE -> "Line - L or 6"
        ActionBarActions.ACTION_ARROW -> "Arrow - A or 5"
        ActionBarActions.ACTION_ELLIPSE -> "Ellipse -  C or 4"
        ActionBarActions.ACTION_DIAMOND -> "Diamond- D or 3"
        ActionBarActions.ACTION_RECT -> "Rectangle - R or 2"
        ActionBarActions.ACTION_SELECT -> "Selection - V or 1"
    }
