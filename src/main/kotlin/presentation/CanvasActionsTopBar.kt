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
import models.ActionBarActions
import models.ActionBarState
import presentation.ui.DrawItAppTheme

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
            ActionBarActionButton(
                isSelected = state.isActionLocked,
                action = ActionBarActions.ACTION_LOCK_CANVAS,
                onClickOrShortcut = { onActionChange(ActionBarActions.ACTION_LOCK_CANVAS) }
            )
            VerticalDivider(thickness = 2.dp)
            // other actions
            ActionBarActions.entries.filterNot { it == ActionBarActions.ACTION_LOCK_CANVAS }
                .sortedBy { it.seqNo }
                .forEach { action ->
                    ActionBarActionButton(
                        isSelected = state.selectedAction == action,
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