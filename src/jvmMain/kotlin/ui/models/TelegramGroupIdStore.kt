package ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TelegramGroupIdStore(
    private var save: (Long) -> Unit,
    load: () -> Long
) {

    var state: String by mutableStateOf((load()).toString())
        private set

    fun editText(text: String) {
        setState {
            text
        }
    }

    fun saveValue() {
        save(state.replace("\n", "").toLong())
        state = state.replace("\n", "")
    }

    private inline fun setState(update: String.() -> String) {
        state = state.update()
    }
}