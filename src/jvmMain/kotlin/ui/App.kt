package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import data.AvitoRepositoryImpl
import data.ConfigurationsRepositoryImpl
import ui.views.GroupIdTextField
import ui.views.LocationTextField
import ui.views.PriceMaxFilterTextField
import ui.views.PriceMinFilterTextField
import ui.views.RefreshTextField
import ui.views.filterListItems.FilterColumn

@ExperimentalUnitApi
@ExperimentalComposeUiApi
@Composable
@Preview
fun App(
    avitoRepository: AvitoRepositoryImpl,
    configs: ConfigurationsRepositoryImpl,
    startBot: () -> Unit,
    stopBot: (Boolean) -> Unit
) {
    val mCheckedState = remember{ mutableStateOf(false)}

    MaterialTheme {

        Column {
            Switch(checked = mCheckedState.value, onCheckedChange = {
                mCheckedState.value = it
                if (it) {
                    startBot()
                } else {
                    stopBot(true)
                }
            })
            Spacer(Modifier.width(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            {
                RefreshTextField(configs)
                Spacer(Modifier.width(32.dp))
                LocationTextField(configs, avitoRepository)
                Spacer(Modifier.width(32.dp))
                GroupIdTextField(configs)
            }
            Spacer(Modifier.width(32.dp))
            Row (
              modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp, end = 8.dp)
            ) {
                PriceMinFilterTextField(configs)
                Spacer(Modifier.width(32.dp))
                PriceMaxFilterTextField(configs)
            }
            Row(
                modifier = Modifier.fillMaxSize().padding(top = 8.dp)
            )
            {
                val columnModifier = Modifier.weight(1f).padding(8.dp)
                FilterColumn(
                    modifier = columnModifier,
                    title = "Фильтр по заголовку",
                    saveList = { list -> configs.titleFilters = list },
                    loadList = { configs.titleFilters }
                )
                FilterColumn(
                    modifier = columnModifier,
                    title = "Фильтр по характеристикам",
                    saveList = { list -> configs.detailsFilter = list },
                    loadList = { configs.detailsFilter }
                )
                FilterColumn(
                    modifier = columnModifier,
                    title = "Фильтр по описанию",
                    saveList = { list -> configs.descriptionsFilter = list },
                    loadList = { configs.descriptionsFilter }
                )
                FilterColumn(
                    modifier = columnModifier,
                    title = "Исключающее слово в заголовке",
                    saveList = { list -> configs.excludeTitleFilter = list },
                    loadList = { configs.excludeTitleFilter }
                )
                FilterColumn(
                    modifier = columnModifier,
                    title = "Исключающее слово в характеристиках",
                    saveList = { list -> configs.excludeDetailsFilter = list },
                    loadList = { configs.excludeDetailsFilter }
                )
                FilterColumn(
                    modifier = columnModifier,
                    title = "Исключающее слово в описании",
                    saveList = { list -> configs.excludeDescriptionFilter = list },
                    loadList = { configs.excludeDescriptionFilter }
                )
            }
        }
    }
}
