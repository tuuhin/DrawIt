package models

import models.actions.ActionBarActions
import models.actions.CanvasDrawAction
import models.actions.CanvasUtilAction

data class ActionBarState(
    val action: ActionBarActions? = null,
    val isCanvasLocked: Boolean = false,
) {
    val hasAction: Boolean
        get() = action != null

    val selectedDrawAction: CanvasDrawAction?
        get() = action as? CanvasDrawAction

    val isSelectAction: Boolean
        get() = action == CanvasUtilAction.ACTION_SELECT

    val isSelectedActionDraw: Boolean
        get() = action is CanvasDrawAction && action == CanvasDrawAction.ACTION_DRAW

    val isSelectedActionPredefinedShape: Boolean
        get() = action is CanvasDrawAction && action != CanvasDrawAction.ACTION_DRAW

}
