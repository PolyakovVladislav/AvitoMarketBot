package ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class RefreshStore(
    private var save: (Long) -> Unit,
    load: () -> Long
) {

    var state: String by mutableStateOf((load() / 1000).toString())
        private set

    fun editText(text: String) {
        setState {
            text
        }
    }

    fun saveValue() {
        save(state.replace("\n", "").toLong() * 1000)
        state = state.replace("\n", "")
    }

    private inline fun setState(update: String.() -> String) {
        state = state.update()
    }
}
