import androidx.compose.ui.geometry.Offset
import event.CanvasDrawStyleChangeEvent
import event.CanvasPropertiesEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.swing.Swing
import models.*
import models.actions.ActionBarActions
import models.actions.CanvasDrawAction
import models.actions.CanvasUtilAction
import kotlin.math.exp

class AppViewModel {

    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Swing + SupervisorJob())

    private val _actionBarState = MutableStateFlow(ActionBarState())
    val actionBarState: StateFlow<ActionBarState>
        get() = _actionBarState

    private val _drawStyle = MutableStateFlow(CanvasDrawStyle())
    val drawStyleState: StateFlow<CanvasDrawStyle>
        get() = _drawStyle

    private val _canvasObjects = MutableStateFlow(CanvasDrawnObjects())
    val canvasObjects: StateFlow<CanvasDrawnObjects>
        get() = _canvasObjects

    private val _canvasScale = MutableStateFlow(1f)
    private val _canvasPan = MutableStateFlow(Offset.Zero)

    // change the object to queue as storing many redo objects is not better
    private val _redoObjects = MutableStateFlow(CanvasDrawnObjects())

    val canvasProperties: StateFlow<CanvasPropertiesState>
        get() {
            val canvasObjectCount = _canvasObjects.map { it.objects.isNotEmpty() }.distinctUntilChanged()
            val redoObjectCount = _redoObjects.map { it.objects.isNotEmpty() }.distinctUntilChanged()

            return combine(
                canvasObjectCount,
                redoObjectCount,
                _canvasPan,
                _canvasScale
            ) { undoEnabled, redoEnabled, pan, scale ->
                CanvasPropertiesState(scale = scale, panedCanvas = pan, undoEnabled, redoEnabled)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = CanvasPropertiesState()
            )
        }


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

    fun onCanvasPropertiesEvent(event: CanvasPropertiesEvent) {
        when (event) {
            CanvasPropertiesEvent.OnRedo -> {
                val lastObject = _redoObjects.value.objects.lastOrNull()
                _redoObjects.update { canvas ->
                    val newList = if (lastObject == null) emptyList()
                    else canvas.objects.dropLast(1)
                    canvas.copy(objects = newList)
                }
                if (lastObject == null) return
                _canvasObjects.update { canvas ->
                    canvas.copy(objects = canvas.objects + lastObject)
                }
            }

            CanvasPropertiesEvent.OnUndo -> {
                val lastObject = _canvasObjects.value.objects.lastOrNull()
                _canvasObjects.update { canvas ->
                    val newList = if (lastObject == null) emptyList()
                    else canvas.objects.dropLast(1)
                    canvas.copy(objects = newList)
                }
                if (lastObject == null) return
                _redoObjects.update { canvas ->
                    canvas.copy(objects = canvas.objects + lastObject)
                }
            }

            CanvasPropertiesEvent.OnResetZoom -> _canvasScale.update { 1f }
            CanvasPropertiesEvent.OnIncrementZoom -> {
                val zoomAmt = (_canvasScale.value + .1f).coerceIn(.25f..3f)
                _canvasScale.update { zoomAmt }
            }

            CanvasPropertiesEvent.OnDecrementZoom -> {
                val zoomAmt = (_canvasScale.value - .1f).coerceIn(.25f..3f)
                _canvasScale.update { zoomAmt }
            }

            is CanvasPropertiesEvent.OnZoom -> {
                val scale = _canvasScale.value
                val updatedAmount =  (scale * exp(event.amount * 0.2f)).coerceIn(0.5f, 3f)
                _canvasScale.update { updatedAmount }
            }

            is CanvasPropertiesEvent.OnPanCanvas -> {
                // -ve sign as we always want out values as negatives
                _canvasPan.update { panAmount -> panAmount + event.amount }
            }
        }
    }

    fun cleanUp() {
        println("VIEW MODEL CLEARED ")
        viewModelScope.cancel()
    }

}