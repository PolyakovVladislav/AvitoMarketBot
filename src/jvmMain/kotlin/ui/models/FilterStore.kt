package ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FilterStore(
    private var saveList: (List<String>) -> Unit,
    private var loadList: () -> List<String>
) {

    var state: FilterState by mutableStateOf(initialState())
        private set

    fun removeItem(index: Int) {
        setState {
            val removingItem = items.get(index)
            val newItems = items.filterNot { item -> item == removingItem }
            saveList(newItems)
            copy(items = newItems)
        }
    }

    fun editText(text: String) {
        setState {
            FilterState (
                items = items,
                inputText = text
            )
        }
    }

    fun addItem() {
        setState {
            val newItems = items + inputText
            saveList(newItems)
            FilterState (
                items = newItems,
                inputText = ""
            )
        }
    }

    private fun initialState(): FilterState =
        FilterState(
            loadList(),
            ""
        )

    private inline fun setState(update: FilterState.() -> FilterState) {
        state = state.update()
    }

    data class FilterState(
        val items: List<String> = emptyList(),
        val inputText: String = ""
    )
}