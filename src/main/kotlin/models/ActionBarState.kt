package models

data class ActionBarState(
    val isActionLocked: Boolean = false,
    val selectedAction: ActionBarActions? = null,
)
