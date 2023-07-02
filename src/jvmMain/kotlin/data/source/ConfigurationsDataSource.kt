package data.source

interface ConfigurationsDataSource {

    var refreshTime: Long

    var locationUrlPart: String

    var telegramGroupId: Long

    var priceMin: Int

    var priceMax: Int

    var titleFilters: List<String>

    var detailsFilter: List<String>

    var descriptionsFilter: List<String>

    var excludeTitleFilter: List<String>

    var excludeDetailsFilter: List<String>

    var excludeDescriptionFilter: List<String>
}