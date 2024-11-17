import androidx.compose.ui.geometry.Offset
import event.CanvasDrawStyleEvent
import event.CanvasItemEvent
import event.CanvasPropertiesEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.swing.Swing
import models.ActionBarState
import models.CanvasDrawStyle
import models.CanvasDrawnObjects
import models.CanvasPropertiesState
import models.actions.ActionBarActions
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
    private val _showGridLines = MutableStateFlow(false)

    // change the object to queue as storing many redo objects is not better
    private val _redoObjects = MutableStateFlow(CanvasDrawnObjects())

    val canvasProperties: StateFlow<CanvasPropertiesState>
        get() {
            val canvasObjectCount = _canvasObjects.map { it.canvasItems.isNotEmpty() }.distinctUntilChanged()
            val redoObjectCount = _redoObjects.map { it.canvasItems.isNotEmpty() }.distinctUntilChanged()

            return combine(
                canvasObjectCount,
                redoObjectCount,
                _canvasPan,
                _canvasScale, _showGridLines
            ) { undoEnabled, redoEnabled, pan, scale, showGrid ->
                CanvasPropertiesState(
                    scale = scale,
                    panedCanvas = pan,
                    undoEnabled,
                    redoEnabled,
                    showGraphLines = showGrid
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = CanvasPropertiesState()
            )
        }


    fun onActionBarAction(action: ActionBarActions) {

        if (action == CanvasUtilAction.ACTION_LOCK_CANVAS) {
            _actionBarState.update { state ->
                val toggleLock = !state.isCanvasLocked
                state.copy(isCanvasLocked = toggleLock)
            }
        } else {
            _actionBarState.update { state ->
                val newState = if (state.action != action) action else null
                state.copy(action = newState)
            }
        }
    }

    fun onCanvasItemEvent(event: CanvasItemEvent) {
        when (event) {
            CanvasItemEvent.OnDeSelectCanvasItem -> _canvasObjects.update { objectModel ->
                objectModel.copy(selectedUUID = null)
            }

            is CanvasItemEvent.OnSelectCanvasItem -> {
                val allItems = _canvasObjects.value.itemsUUIDS
                if (event.itemUUID !in allItems) return
                _canvasObjects.update { objectModel -> objectModel.copy(selectedUUID = event.itemUUID) }
            }

            is CanvasItemEvent.OnAddNewCanvasItem -> {
                val newSelection = if (_actionBarState.value.isCanvasLocked) null else event.item
                _canvasObjects.update { canvasObject ->
                    canvasObject.copy(
                        canvasItems = canvasObject.canvasItems + event.item,
                        selectedUUID = event.item.uuid
                    )
                }
                // update it to select mode
                newSelection?.let {
                    _actionBarState.update { state -> state.copy(action = CanvasUtilAction.ACTION_SELECT) }
                }
            }

            is CanvasItemEvent.OnMoveSelectedItem -> {
                val items = _canvasObjects.value.canvasItems.map { item ->
                    if (event.itemUUID == item.uuid) with(item) {
                        copy(
                            start = start + event.panOffset,
                            end = end + event.panOffset
                        )
                    } else item
                }
                _canvasObjects.update { itemsObject -> itemsObject.copy(canvasItems = items) }
            }

            is CanvasItemEvent.OnResizeSelectedItem -> {
                val items = _canvasObjects.value.canvasItems.map { item ->
                    if (event.itemUUID == item.uuid)
                        with(event.newRect) { item.copy(start = topLeft, end = bottomRight) }
                    else item
                }
                _canvasObjects.update { itemsObject -> itemsObject.copy(canvasItems = items) }
            }

            is CanvasItemEvent.OnRotateSelectedItem -> {
                val items = _canvasObjects.value.canvasItems.map { item ->
                    if (event.itemUUID == item.uuid) item.copy(rotateInDegrees = event.degree)
                    else item
                }
                _canvasObjects.update { itemsObject -> itemsObject.copy(canvasItems = items) }
            }
        }
    }


    fun onDrawStyleChange(event: CanvasDrawStyleEvent) {
        when (event) {
            is CanvasDrawStyleEvent.OnAlphaChange -> _drawStyle.update { state -> state.copy(alpha = event.alpha) }
            is CanvasDrawStyleEvent.OnBackgroundColorChange -> _drawStyle.update { state -> state.copy(background = event.colorOptions) }
            is CanvasDrawStyleEvent.OnPathEffectChange -> _drawStyle.update { state -> state.copy(pathEffect = event.pathEffectOptions) }
            is CanvasDrawStyleEvent.OnStrokeColorChange -> _drawStyle.update { state -> state.copy(strokeColor = event.colorOptions) }
            is CanvasDrawStyleEvent.OnStrokeOptionChange -> _drawStyle.update { state -> state.copy(strokeOption = event.option) }
            is CanvasDrawStyleEvent.OnRoundnessChange -> _drawStyle.update { state -> state.copy(roundness = event.roundness) }
        }
        // update the selected object
        val canvasObjects = _canvasObjects.value
        // update if there is a selected object
        canvasObjects.selectedUUID?.let { selectedObject ->
            val result = canvasObjects.canvasItems.map { item ->
                if (item.uuid == selectedObject) item.copy(style = _drawStyle.value)
                else item
            }
            _canvasObjects.update { it.copy(canvasItems = result) }
        }
    }

    fun onCanvasPropertiesEvent(event: CanvasPropertiesEvent) {
        when (event) {
            CanvasPropertiesEvent.OnRedo -> {
                val lastObject = _redoObjects.value.canvasItems.lastOrNull()
                _redoObjects.update { canvas ->
                    val newList = if (lastObject == null) emptyList()
                    else canvas.canvasItems.dropLast(1)
                    canvas.copy(canvasItems = newList)
                }
                if (lastObject == null) return
                _canvasObjects.update { canvas ->
                    canvas.copy(canvasItems = canvas.canvasItems + lastObject)
                }
            }

            CanvasPropertiesEvent.OnUndo -> {
                val lastObject = _canvasObjects.value.canvasItems.lastOrNull()
                _canvasObjects.update { canvas ->
                    val newList = if (lastObject == null) emptyList()
                    else canvas.canvasItems.dropLast(1)
                    canvas.copy(canvasItems = newList)
                }
                if (lastObject == null) return
                _redoObjects.update { canvas ->
                    canvas.copy(canvasItems = canvas.canvasItems + lastObject)
                }
            }

            CanvasPropertiesEvent.OnResetZoom -> _canvasScale.update { 1f }
            CanvasPropertiesEvent.OnIncrementZoom -> {
                val zoomAmt = (_canvasScale.value + .1f).coerceIn(.3f..3f)
                _canvasScale.update { zoomAmt }
            }

            CanvasPropertiesEvent.OnDecrementZoom -> {
                val zoomAmt = (_canvasScale.value - .1f).coerceIn(.3f..3f)
                _canvasScale.update { zoomAmt }
            }

            is CanvasPropertiesEvent.OnZoom -> {
                val scale = _canvasScale.value
                val updatedAmount = (scale * exp(event.amount * 0.2f)).coerceIn(0.3f..3f)
                _canvasScale.update { updatedAmount }
            }

            is CanvasPropertiesEvent.OnPanCanvas -> {
                _canvasPan.update { panAmount -> panAmount + event.amount }
            }

            CanvasPropertiesEvent.ToggleGridLinesVisibility -> _showGridLines.update { it.not() }
        }
    }

    fun cleanUp() {
        println("VIEW MODEL CLEARED ")
        viewModelScope.cancel()
    }

}