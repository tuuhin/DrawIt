package models

import models.actions.CanvasDrawAction

data class ActionBarState(
    val isActionLocked: Boolean = false,
    val selectedDrawAction: CanvasDrawAction? = null,
)
