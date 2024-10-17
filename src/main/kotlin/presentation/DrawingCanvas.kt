package presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import models.ActionBarState
import presentation.ui.DrawItAppTheme

@Composable
fun DrawingCanvas(state: ActionBarState, modifier: Modifier = Modifier) {

}


@Preview
@Composable
fun DrawingCanvasPreview() = DrawItAppTheme {
    DrawingCanvas(state = ActionBarState())
}