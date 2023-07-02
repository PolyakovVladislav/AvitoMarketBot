package data

import data.source.local.ConfigurationsLocalDataSource
import data.source.local.NotebookHistoryLocalDataSource
import data.source.remote.NotebookRemoteDataSource
import domain.AvitoRepository
import domain.exceptions.NotebookDetailsParsingException
import domain.models.LocationSuggestion
import domain.models.NotebookDetails
import domain.models.Notebook

class AvitoRepositoryImpl(
    private val dataSource: NotebookRemoteDataSource,
    private val historyDataSource: NotebookHistoryLocalDataSource,
    private val configurationsDataSource: ConfigurationsLocalDataSource
): AvitoRepository {

    override suspend fun getFreshList(): Result<List<Notebook>> {
        val freshList = dataSource.getList(configurationsDataSource.locationUrlPart)
        val filteredList = freshList.filter { notebook ->
            val result = if (historyDataSource.isNotebookAlreadyChecked(notebook)) {
                val priceDifference = historyDataSource.getPriceDifference(notebook)
                if (priceDifference < 0) {
                    historyDataSource.overrideRecord(notebook)
                    true
                } else {
                    false
                }
            }
            else {
                true
            }
            result
        }
        return Result.success(filteredList)
    }

    override suspend fun getNotebookDetails(notebook: Notebook): Result<NotebookDetails> {
        return try {
            val notebookDetails = dataSource.getNotebookDetails(notebook)
            historyDataSource.recordNotebook(notebook)
            Result.success(notebookDetails)
        } catch (e: NotebookDetailsParsingException) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun closeWebDriver(): Result<Unit> {
        return Result.success(dataSource.closeWebDriver())
    }

    override suspend fun getLocationsForInput(input: String): Result<List<LocationSuggestion>> {
        return Result.success(dataSource.getLocationForInput(input))
    }

    override suspend fun getLocationPartForUrl(locationSuggestion: LocationSuggestion): Result<String> {
        return Result.success(dataSource.getLocationPartForUrl(locationSuggestion))
    }
}