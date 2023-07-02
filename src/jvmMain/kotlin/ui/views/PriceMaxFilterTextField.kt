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
import ui.models.PriceStore
import ui.views.utils.onKeyUp

@ExperimentalComposeUiApi
@ExperimentalUnitApi
@Composable
fun PriceMaxFilterTextField(
    configs: ConfigurationsRepositoryImpl
) {
    val store = remember {
        PriceStore(
            save = { value -> configs.priceMax = value },
            load = { configs.priceMax }
        )
    }
    val state = store.state

    OutlinedTextField(
        value = state,
        modifier = Modifier.onKeyUp(key = Key.Enter, action = store::saveValue).width(250.dp),
        onValueChange = store::editText,
        label = {
            Text(
                text = "Цена максимальная",
                fontSize = TextUnit(10f, TextUnitType.Sp)
            )
        }
    )
}
