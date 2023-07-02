package ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PriceStore(
    private var save: (Int) -> Unit,
    load: () -> Int
) {

    var state: String by mutableStateOf((load()).toString())
        private set

    fun editText(text: String) {
        setState {
            text
        }
    }

    fun saveValue() {
        save(state.replace("\n", "").toInt())
        state = state.replace("\n", "")
    }

    private inline fun setState(update: String.() -> String) {
        state = state.update()
    }
}