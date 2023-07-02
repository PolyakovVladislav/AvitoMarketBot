package ui.views.filterListItems

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import ui.models.FilterStore

@ExperimentalUnitApi
@ExperimentalComposeUiApi
@Composable
fun FilterColumn(
    modifier: Modifier,
    title: String,
    saveList: (List<String>) -> Unit,
    loadList: () -> List<String>
) {
    val filterList = remember { FilterStore(
        saveList = { list -> saveList(list) },
        loadList = { loadList() }
    ) }
    val state = filterList.state

    ui.views.List(
        items = state.items,
        inputText = state.inputText,
        title = title,
        modifier = modifier,
        onItemClicked = filterList::removeItem,
        onAddItemClicked = filterList::addItem,
        onInputTextChanged = filterList::editText
    )
}
