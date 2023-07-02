package ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.AvitoRepositoryImpl
import data.ConfigurationsRepositoryImpl
import domain.AvitoRepository
import domain.models.LocationSuggestion

class LocationStore(
    private var configs: ConfigurationsRepositoryImpl,
    private var avitoRepository: AvitoRepositoryImpl
) {

    var state: String by mutableStateOf(configs.locationUrlPart)
        private set

    fun editText(text: String) {
        configs.locationUrlPart = text
        setState {
            text
        }
    }

    private inline fun setState(update: String.() -> String) {
        state = state.update()
    }
}