package models.actions

enum class CanvasDrawAction(val seqNo: Int) : ActionBarActions {
    ACTION_RECT(2),
    ACTION_DIAMOND(3),
    ACTION_ELLIPSE(4),
    ACTION_ARROW(5),
    ACTION_LINE(6),
    ACTION_DRAW(7),
    ACTION_TEXT(8);

    val canHaveBoundary: Boolean
        get() = this in arrayOf(ACTION_RECT, ACTION_DIAMOND, ACTION_ELLIPSE, ACTION_DRAW)
}