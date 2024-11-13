package presentation.style_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.eva.draw_it.drawit.generated.resources.Res
import com.eva.draw_it.drawit.generated.resources.style_option_roundness_title
import mapper.painterRes
import models.canvas.CornerRoundnessOption
import org.jetbrains.compose.resources.stringResource

@Composable
fun CornerRadiusPicker(
    option: CornerRoundnessOption,
    onOptionChange: (CornerRoundnessOption) -> Unit,
    modifier: Modifier = Modifier,
    headingStyle: TextStyle = MaterialTheme.typography.labelMedium,
    headingColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedContainer: Color = MaterialTheme.colorScheme.primaryContainer,
    onSelectedContainer: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unSelectedContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    onUnSelectedContainer: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(Res.string.style_option_roundness_title),
            color = headingColor,
            style = headingStyle,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CornerRoundnessOption.entries.forEach { roundness ->
                val isSelected = option == roundness

                Box(
                    modifier = Modifier.size(32.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(if (isSelected) selectedContainer else unSelectedContainerColor)
                        .clickable { onOptionChange(roundness) }
                ) {
                    Icon(
                        painter = roundness.painterRes,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        tint = if (isSelected) onSelectedContainer else onUnSelectedContainer
                    )
                }
            }
        }
    }
}