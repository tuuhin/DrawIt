package utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.painterResource
import models.ActionBarActions

val ActionBarActions.actionPainter: Painter
    @Composable
    get() = when (this) {
        ActionBarActions.ACTION_ARROW -> painterResource("images/ic_arrow.svg")
        ActionBarActions.ACTION_DIAMOND -> painterResource("images/ic_rhombus.svg")
        ActionBarActions.ACTION_DRAW -> painterResource("images/ic_pen.svg")
        ActionBarActions.ACTION_ELLIPSE -> painterResource("images/ic_circle.svg")
        ActionBarActions.ACTION_ERASER -> painterResource("images/ic_eraser.svg")
        ActionBarActions.ACTION_IMAGE -> painterResource("images/ic_image.svg")
        ActionBarActions.ACTION_LINE -> painterResource("images/ic_dash.svg")
        ActionBarActions.ACTION_RECT -> painterResource("images/ic_square.svg")
        ActionBarActions.ACTION_SELECT -> painterResource("images/ic_pointer.svg")
        ActionBarActions.ACTION_TEXT -> painterResource("images/ic_text.svg")
        ActionBarActions.ACTION_LOCK_CANVAS -> painterResource("images/ic_lock.svg")
        ActionBarActions.ACTION_HAND -> painterResource("images/ic_hand.svg")
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
