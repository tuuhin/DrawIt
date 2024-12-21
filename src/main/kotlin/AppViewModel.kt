import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import event.CanvasDrawStyleEvent
import event.CanvasItemEvent
import event.CanvasPropertiesEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.swing.Swing
import models.ActionBarState
import models.CanvasDrawStyle
import models.CanvasDrawnObjects
import models.CanvasPropertiesState
import models.actions.ActionBarActions
import models.actions.CanvasUtilAction
import kotlin.math.exp

class AppViewModel : ViewModel(viewModelScope = CoroutineScope(Dispatchers.Swing + SupervisorJob())) {

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
                    canvasScale = scale,
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
                val updatedList = _canvasObjects.value.updateMatchingItem(
                    predicate = { it.uuid == event.itemUUID },
                    update = { item -> item.updateItemPositionForPan(event.panOffset) },
                )
                _canvasObjects.update { itemsObject -> itemsObject.copy(canvasItems = updatedList) }
            }

            is CanvasItemEvent.OnResizeSelectedItem -> {
                val updatedList = _canvasObjects.value.updateMatchingItem(
                    predicate = { it.uuid == event.itemUUID },
                    update = { item -> item.updateItemOnResize(event.newRect) },
                )

                _canvasObjects.update { itemsObject -> itemsObject.copy(canvasItems = updatedList) }
            }

            is CanvasItemEvent.OnRotateSelectedItem -> {
                val updatedList = _canvasObjects.value.updateMatchingItem(
                    predicate = { it.uuid == event.itemUUID },
                    update = { item -> item.updateItemOnRotate(event.radians) },
                )
                _canvasObjects.update { itemsObject -> itemsObject.copy(canvasItems = updatedList) }
            }
        }
    }


    fun onDrawStyleChange(event: CanvasDrawStyleEvent) {
        when (event) {
            is CanvasDrawStyleEvent.OnAlphaChange -> _drawStyle.update { state -> state.copy(alpha = event.alpha) }
            is CanvasDrawStyleEvent.OnBackgroundColorChange -> _drawStyle.update { state ->
                state.onBackgroundColorChange(event.colorOptions)
            }

            is CanvasDrawStyleEvent.OnPathEffectChange -> _drawStyle.update { state -> state.copy(pathEffect = event.pathEffectOptions) }
            is CanvasDrawStyleEvent.OnStrokeColorChange -> _drawStyle.update { state -> state.copy(strokeColor = event.colorOptions) }
            is CanvasDrawStyleEvent.OnStrokeOptionChange -> _drawStyle.update { state -> state.copy(strokeOption = event.option) }
            is CanvasDrawStyleEvent.OnRoundnessChange -> _drawStyle.update { state -> state.copy(roundness = event.roundness) }
            is CanvasDrawStyleEvent.OnBackgroundFillChange -> _drawStyle.update { state -> state.copy(backgroundFill = event.fill) }
        }

        val updatedList = _canvasObjects.value.updateMatchingItem(
            predicate = { it.uuid == canvasObjects.value.selectedUUID },
            update = { item ->
                _canvasObjects.value.selectedItem?.copy(style = _drawStyle.value) ?: item
            },
        )
        _canvasObjects.update { itemsObject -> itemsObject.copy(canvasItems = updatedList) }
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

            is CanvasPropertiesEvent.OnPanCanvas -> _canvasPan.update { panAmount -> panAmount + event.amount }
            CanvasPropertiesEvent.OnResetZoom -> _canvasScale.update { 1f }
            CanvasPropertiesEvent.ToggleGridLinesVisibility -> _showGridLines.update { it.not() }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Logger.d { "VIEW MODEL CLEARED" }
    }
}