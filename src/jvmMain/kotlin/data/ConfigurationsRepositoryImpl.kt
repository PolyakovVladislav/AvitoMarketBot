package data

import data.source.local.ConfigurationsLocalDataSource
import data.source.local.NotebookHistoryLocalDataSource
import domain.ConfigurationsRepository

class ConfigurationsRepositoryImpl(
    private val configurationsLocalDataSource: ConfigurationsLocalDataSource,
    private val notebookHistoryLocalDataSource: NotebookHistoryLocalDataSource
): ConfigurationsRepository {

    override var refreshTime: Long
        get() = configurationsLocalDataSource.refreshTime
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.refreshTime = value
        }

    override var locationUrlPart: String
        get() = configurationsLocalDataSource.locationUrlPart
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.locationUrlPart = value
        }

    override var telegramGroupId: Long
        get() = configurationsLocalDataSource.telegramGroupId
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.telegramGroupId = value
        }

    override var priceMin: Int
        get() = configurationsLocalDataSource.priceMin
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.priceMin = value
        }

    override var priceMax: Int
        get() = configurationsLocalDataSource.priceMax
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.priceMax = value
        }

    override var titleFilters: List<String>
        get() = configurationsLocalDataSource.titleFilters
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.titleFilters = value
        }

    override var detailsFilter: List<String>
        get() = configurationsLocalDataSource.detailsFilter
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.detailsFilter = value
        }

    override var descriptionsFilter: List<String>
        get() = configurationsLocalDataSource.descriptionsFilter
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.descriptionsFilter = value
        }

    override var excludeTitleFilter: List<String>
        get() = configurationsLocalDataSource.excludeTitleFilter
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.excludeTitleFilter = value
        }

    override var excludeDetailsFilter: List<String>
        get() = configurationsLocalDataSource.excludeDetailsFilter
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.excludeDetailsFilter = value
        }

    override var excludeDescriptionFilter: List<String>
        get() = configurationsLocalDataSource.excludeDescriptionFilter
        set(value) {
            notebookHistoryLocalDataSource.clearHistory()
            configurationsLocalDataSource.excludeDescriptionFilter = value
        }
}