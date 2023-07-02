package ui.views

import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import data.ConfigurationsRepositoryImpl
import ui.models.RefreshStore
import ui.views.utils.onKeyUp

@ExperimentalComposeUiApi
@ExperimentalUnitApi
@Composable
fun RefreshTextField(
    configs: ConfigurationsRepositoryImpl
) {

    val refreshStore = remember { RefreshStore(
        save = { value -> configs.refreshTime = value },
        load = { configs.refreshTime }
    ) }
    val state = refreshStore.state

    OutlinedTextField(
        value = state,
        modifier = Modifier.onKeyUp(key = Key.Enter, action = refreshStore::saveValue).width(150.dp),
        onValueChange = refreshStore::editText,
        label = {
            Text(
                text = "Частота запросов (секунды)",
                fontSize = TextUnit(10f, TextUnitType.Sp)
            )
        }
    )
}
