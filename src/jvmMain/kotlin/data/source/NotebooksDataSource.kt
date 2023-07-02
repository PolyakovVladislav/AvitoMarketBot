package data.source

import domain.models.LocationSuggestion
import domain.models.NotebookDetails
import domain.models.Notebook

interface NotebooksDataSource {

    suspend fun getList(location: String): List<Notebook>

    suspend fun getNotebookDetails(notebook: Notebook): NotebookDetails

    suspend fun getLocationForInput(input: String): List<LocationSuggestion>

    fun getLocationPartForUrl(locationSuggestion: LocationSuggestion): String

    suspend fun closeWebDriver()

}