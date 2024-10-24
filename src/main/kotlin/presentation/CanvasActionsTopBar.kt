package presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import models.ActionBarState
import models.actions.ActionBarActions
import models.actions.CanvasDrawAction
import models.actions.CanvasUtilAction
import ui.DrawItAppTheme

@Composable
fun CanvasActionsTopBar(
    state: ActionBarState,
    onActionChange: (ActionBarActions) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    elevation: Dp = 4.dp,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    Surface(
        color = containerColor,
        shape = shape,
        modifier = modifier.shadow(elevation = elevation, shape = shape)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min).padding(all = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // lock action has its own speciality to keep differently
            CanvasUtilAction.entries.forEach { action ->
                ActionBarActionButton(
                    isSelected = state.isActionLocked,
                    action = action,
                    onClickOrShortcut = { onActionChange(action) }
                )
                if (action == CanvasUtilAction.ACTION_LOCK_CANVAS)
                    VerticalDivider(thickness = 2.dp)
            }
            // other actions
            CanvasDrawAction.entries.sortedBy { it.seqNo }.forEach { action ->
                ActionBarActionButton(
                    isSelected = state.selectedDrawAction == action,
                    action = action,
                    onClickOrShortcut = { onActionChange(action) }
                )
            }
        }
    }
}

@Preview
@Composable
fun ActionBarPreview() = DrawItAppTheme {
        CanvasActionsTopBar(
            state = ActionBarState(),
            onActionChange = {}
        )
}