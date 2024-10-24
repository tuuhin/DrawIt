import event.CanvasDrawStyleChangeEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import models.ActionBarState
import models.CanvasDrawStyle
import models.CanvasDrawnObjects
import models.CanvasItemModel
import models.actions.ActionBarActions
import models.actions.CanvasDrawAction
import models.actions.CanvasUtilAction

class AppViewModel {

    private val _actionBarState = MutableStateFlow(ActionBarState())
    val actionBarState: StateFlow<ActionBarState>
        get() = _actionBarState

    private val _drawStyle = MutableStateFlow(CanvasDrawStyle())
    val drawStyleState: StateFlow<CanvasDrawStyle>
        get() = _drawStyle

    private val _canvasObjects = MutableStateFlow(CanvasDrawnObjects())
    val canvasObjects: StateFlow<CanvasDrawnObjects>
        get() = _canvasObjects


    fun onActionBarAction(action: ActionBarActions) {

        if (action == CanvasUtilAction.ACTION_LOCK_CANVAS) {
            _actionBarState.update { state ->
                val toggleLock = !state.isActionLocked
                state.copy(isActionLocked = toggleLock)
            }
        } else if (action is CanvasDrawAction) {
            _actionBarState.update { state ->
                val newState = if (state.selectedDrawAction != action) action else null
                state.copy(selectedDrawAction = newState)
            }
        }
    }

    fun onAddNewObject(newObject: CanvasItemModel) {
        _canvasObjects.update { objectModel ->
            objectModel.copy(objects = objectModel.objects + newObject)
        }
    }

    fun onDrawStyleChange(event: CanvasDrawStyleChangeEvent) {
        when (event) {
            is CanvasDrawStyleChangeEvent.OnAlphaChange -> _drawStyle.update { state -> state.copy(alpha = event.alpha) }
            is CanvasDrawStyleChangeEvent.OnBackgroundColorChange -> _drawStyle.update { state -> state.copy(background = event.colorOptions) }
            is CanvasDrawStyleChangeEvent.OnPathEffectChange -> _drawStyle.update { state -> state.copy(pathEffect = event.pathEffectOptions) }
            is CanvasDrawStyleChangeEvent.OnStrokeColorChange -> _drawStyle.update { state -> state.copy(strokeColor = event.colorOptions) }
            is CanvasDrawStyleChangeEvent.OnStrokeOptionChange -> _drawStyle.update { state -> state.copy(strokeOption = event.option) }
        }
    }

}