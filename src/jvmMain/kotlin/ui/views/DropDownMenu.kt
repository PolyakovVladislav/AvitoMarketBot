package ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.models.LocationSuggestion

@Composable
fun showDropDown(
    list: List<LocationSuggestion>,
    onItemPicked: (Int) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded.value = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Settings")
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            list.forEachIndexed { index, locationSuggestion ->
                DropdownMenuItem(onClick = {
                    onItemPicked(index)
                }) {
                    Text(locationSuggestion.suggestion)
                }
            }

        }
    }
}