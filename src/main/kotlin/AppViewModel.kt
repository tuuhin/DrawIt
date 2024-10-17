import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import models.ActionBarActions
import models.ActionBarState

class AppViewModel {

    private val _actionBarState = MutableStateFlow(ActionBarState())
    val actionBarState: StateFlow<ActionBarState>
        get() = _actionBarState

    fun onActionBarAction(action: ActionBarActions) {
        if (action == ActionBarActions.ACTION_LOCK_CANVAS) {
            _actionBarState.update { state ->
                val toggleLock = !state.isActionLocked
                state.copy(isActionLocked = toggleLock)
            }
        } else {
            _actionBarState.update { state ->
                val newState = if (state.selectedAction != action) action else null
                state.copy(selectedAction = newState)
            }
        }
    }
}