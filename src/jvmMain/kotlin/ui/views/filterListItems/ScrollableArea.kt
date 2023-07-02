package ui.views

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import ui.views.utils.onKeyUp

@ExperimentalUnitApi
@ExperimentalComposeUiApi
@Composable
fun List(
    items: List<String>,
    inputText: String,
    title: String,
    modifier: Modifier,
    onItemClicked: (id: Int) -> Unit,
    onAddItemClicked: () -> Unit,
    onInputTextChanged: (String) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        val stateVertical = rememberScrollState(0)
        Column(modifier = Modifier.verticalScroll(stateVertical)) {

            OutlinedTextField(
                value = inputText,
                modifier = Modifier.onKeyUp(key = Key.Enter, action = onAddItemClicked).fillMaxSize(),
                onValueChange = onInputTextChanged,
                label = { Text(
                    text = title,
                    fontSize = TextUnit(10f, TextUnitType.Sp)
                )
                }
            )

            items.forEachIndexed { index, text ->
                ListItem(
                    index,
                    text,
                    onItemClicked
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(stateVertical),
            modifier = Modifier.align(Alignment.CenterEnd)
                .fillMaxHeight()
        )
    }
}