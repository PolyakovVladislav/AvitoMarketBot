package data.source.local

import data.source.ConfigurationsDataSource
import java.io.File

class ConfigurationsLocalDataSource: ConfigurationsDataSource {

    private val configDirectory = File("${System.getProperty("user.home")}\\AppData\\Local\\AvitoBot")

    private val refreshFile = File("$configDirectory\\refresh")
    private val localFile = File("$configDirectory\\local")
    private val groupIdFile = File("$configDirectory\\groupId")

    private val priceMinFile = File("$configDirectory\\priceMin")
    private val priceMaxFile = File("$configDirectory\\priceMAx")

    private val titleFiltersFile = File("$configDirectory\\title filters")
    private val detailsFiltersFile = File("$configDirectory\\details filters")
    private val descriptionsFiltersFile = File("$configDirectory\\descriptions filters")

    private val excludeTitleFiltersFile = File("$configDirectory\\title filters exclude")
    private val excludeDetailsFiltersFile = File("$configDirectory\\details filters exclude")
    private val excludeDescriptionsFiltersFile = File("$configDirectory\\descriptions filters exclude")

    init {
        if (configDirectory.exists().not()) {
            configDirectory.mkdirs()
        }
    }

    override var refreshTime: Long
        get() {
            val refreshTime = readFile(refreshFile)?.first()?.toLong()
            return if (refreshTime == 0L || refreshTime == null) {
                60000L
            } else {
                refreshTime
            }
        }
        set(value) = writeFile(refreshFile, listOf(value.toString()))

    override var locationUrlPart: String
        get() = readFile(localFile)?.first() ?: "sankt-peterburg"
        set(value) = writeFile(localFile, listOf(value))

    override var telegramGroupId: Long
        get() = readFile(groupIdFile)?.first()?.toLong() ?: -1L
        set(value) = writeFile(groupIdFile, listOf(value.toString()))

    override var priceMin: Int
        get() = readFile(priceMinFile)?.first()?.toInt() ?: 0
        set(value) { writeFile(priceMinFile, listOf(value.toString())) }

    override var priceMax: Int
        get() = readFile(priceMaxFile)?.first()?.toInt() ?: 0
        set(value) { writeFile(priceMaxFile, listOf(value.toString())) }

    override var titleFilters: List<String>
        get() = readFile(titleFiltersFile) ?: emptyList()
        set(value) = writeFile(titleFiltersFile, value)

    override var detailsFilter: List<String>
        get() = readFile(detailsFiltersFile) ?: emptyList()
        set(value) = writeFile(detailsFiltersFile, value)

    override var descriptionsFilter: List<String>
        get() = readFile(descriptionsFiltersFile) ?: emptyList()
        set(value) = writeFile(descriptionsFiltersFile, value)

    override var excludeTitleFilter: List<String>
        get() = readFile(excludeTitleFiltersFile) ?: emptyList()
        set(value) = writeFile(excludeTitleFiltersFile, value)

    override var excludeDetailsFilter: List<String>
        get() = readFile(excludeDetailsFiltersFile) ?: emptyList()
        set(value) = writeFile(excludeDetailsFiltersFile, value)

    override var excludeDescriptionFilter: List<String>
        get() = readFile(excludeDescriptionsFiltersFile) ?: emptyList()
        set(value) = writeFile(excludeDescriptionsFiltersFile, value)

    private fun writeFile(file: File, list: List<String>) {
        val writer = file.bufferedWriter()
        writer.flush()
        list.forEachIndexed { index, line ->
            writer.write(line)
            if (list.size != index + 1) {
                writer.newLine()
            }
        }
        writer.close()
    }

    private fun readFile(file: File): List<String>? {
        return if (file.exists()) {
            file.readLines()
        } else {
            null
        }
    }
}