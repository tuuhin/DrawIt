package presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mapper.actionPainter
import mapper.keyboardShortcuts
import mapper.overlayText
import mapper.tooltipText
import models.actions.ActionBarActions

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionBarActionButton(
    isSelected: Boolean,
    action: ActionBarActions,
    onClickOrShortcut: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    onColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    onSelectedColor: Color = MaterialTheme.colorScheme.onPrimary,
    hoveredColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    onHoveredColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val containerColor by animateColorAsState(
        targetValue = when {
            isSelected -> selectedColor
            isHovered -> hoveredColor
            else -> color
        },
        animationSpec = tween(durationMillis = 100, easing = EaseInOut)
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isSelected -> onSelectedColor
            isHovered -> onHoveredColor
            else -> onColor
        },
        animationSpec = tween(durationMillis = 100, easing = EaseInOut)
    )

    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(elevation = 4.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = action.tooltipText,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(4.dp)
                )
            }
        },
        delayMillis = 600, // in milliseconds
        modifier = modifier.sizeIn(maxWidth = 36.dp, maxHeight = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .background(containerColor, shape = MaterialTheme.shapes.small)
                .hoverable(interactionSource, enabled = true)
                .pointerHoverIcon(icon = PointerIcon.Hand)
                .onClick(onClick = onClickOrShortcut)
                .onKeyEvent { keyEvent ->
                    val shortcutRead = keyEvent.key in (action.keyboardShortcuts)
                    if (shortcutRead) onClickOrShortcut()
                    return@onKeyEvent shortcutRead
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = action.actionPainter,
                contentDescription = action.tooltipText,
                tint = contentColor,
                modifier = Modifier.padding(all = 8.dp).size(20.dp).align(Alignment.Center),
            )
            action.overlayText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Thin,
                    color = contentColor,
                    modifier = Modifier.padding(end = 4.dp, bottom = 1.dp).align(Alignment.BottomEnd)
                )
            }
        }
    }
}