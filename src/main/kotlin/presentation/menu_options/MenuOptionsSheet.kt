package presentation.menu_options

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.ic_menu
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuOptionsSheet(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    Surface(
        color = containerColor,
        tonalElevation = elevation,
        shape = shape,
        modifier = modifier.clip(shape)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable { }
    ) {
        TooltipArea(
            tooltip = {
                Surface(
                    modifier = Modifier.shadow(elevation = 4.dp),
                    color = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Menu ",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            },
            delayMillis = 600, // in milliseconds
            modifier = modifier.sizeIn(maxWidth = 36.dp, maxHeight = 36.dp),
        ) {
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_menu),
                    contentDescription = "Menu Bar",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
                )
            }
        }
    }
}