package ui.views

import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import data.AvitoRepositoryImpl
import data.ConfigurationsRepositoryImpl
import ui.models.LocationStore

@ExperimentalComposeUiApi
@ExperimentalUnitApi
@Composable
fun LocationTextField(
    configs: ConfigurationsRepositoryImpl,
    avitoRepository: AvitoRepositoryImpl
) {

    val locationStore = remember {
        LocationStore(configs, avitoRepository)
    }
    val state = locationStore.state

    OutlinedTextField(
        value = state,
        modifier = Modifier.width(300.dp),
        onValueChange = locationStore::editText,
        label = {
            Text(
                text = "Локация (из url)",
                fontSize = TextUnit(10f, TextUnitType.Sp)
            )
        }
    )
}
