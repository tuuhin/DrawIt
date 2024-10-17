package models

enum class ActionBarActions(val seqNo: Int) {
    // these are also draw action, but the role is different
    ACTION_LOCK_CANVAS(-2),
    ACTION_HAND(-1),

    // drawing actions
    ACTION_SELECT(1),
    ACTION_RECT(2),
    ACTION_DIAMOND(3),
    ACTION_ELLIPSE(4),
    ACTION_ARROW(5),
    ACTION_LINE(6),
    ACTION_DRAW(7),
    ACTION_TEXT(8),
    ACTION_IMAGE(9),
    ACTION_ERASER(10),
}