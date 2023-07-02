package domain

import domain.models.LocationSuggestion
import domain.models.NotebookDetails
import domain.models.Notebook

interface AvitoRepository {


    suspend fun getFreshList(): Result<List<Notebook>>

    suspend fun getNotebookDetails(notebook: Notebook): Result<NotebookDetails>

    suspend fun closeWebDriver(): Result<Unit>

    suspend fun getLocationsForInput(input: String): Result<List<LocationSuggestion>>

    suspend fun getLocationPartForUrl(locationSuggestion: LocationSuggestion): Result<String>
}