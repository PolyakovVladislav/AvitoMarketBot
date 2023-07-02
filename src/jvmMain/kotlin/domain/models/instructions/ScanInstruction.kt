package domain.models.instructions

import data.AvitoRepositoryImpl
import data.ConfigurationsRepositoryImpl
import domain.exceptions.NotebookDetailsParsingException
import domain.models.Instruction
import domain.models.Notebook
import kotlinx.coroutines.delay
import messaging.TelegramMessengerImpl
import utils.Logger

/**
 * Пояснение к устройству фильтров:
 * 1) Бот просматривает список доступных ноутбуков и исключает из полученного списка ноуты где есть хоть
 * одно слово из фильтров: "Исключающее слово в заголовке", "Исключающее слово в характеристиках" и
 * фильтрует ноутбуки по ценовому диапазону. Если хоть одно условия для исключения выполняется - пост пропускается
 * 2) Отфильтрованный список фильтруется снова по фильтрам: "Фильтр по заголовку", "Фильтр по описанию".
 * Если хотя бы одно слово из этого фильтра есть в соответствующем поле (Заголовок или описание).
 * Оставляй пустыми если не нужно
 * 3) Полученный список постов бот просматривает и оценивает по фильтрам: "Фильтр по характеристикам" и
 * "Исключающее слово в характеристиках". Если найдено одно слово (или словосочетание)  из "Фильтр по характеристикам",
 * то ноутбук отправляется в чат. Если найдено хоть одно слово (или словосочетание) из "Исключающее слово в
 * характеристиках", то ноут пропускается. Оставь фильтры пустыми если не нужны.
 */

class ScanInstruction(
    id: Int,
    executionTime: Long,
    possibleDelay: Long,
    priority: Int,
    timeout: Long,
    onExecuted: (Instruction) -> Unit,
    description: String,
    private val avitoRepository: AvitoRepositoryImpl,
    private val configs: ConfigurationsRepositoryImpl,
    private val telegramMessenger: TelegramMessengerImpl

) : Instruction(id, executionTime, possibleDelay, priority, timeout, onExecuted, description) {

    val log = Logger(this.javaClass.name)

    override suspend fun run() {
        try {
            val result = avitoRepository.getFreshList()

            val excludeTitleFilter = configs.excludeTitleFilter
            val excludeDetailsFilter = configs.excludeDetailsFilter
            val excludeDescriptionFilter = configs.excludeDescriptionFilter
            val titleFilters = configs.titleFilters
            val descriptionsFilter = configs.descriptionsFilter
            val detailsFilter = configs.detailsFilter
            val priceMin = configs.priceMin
            val priceMax = configs.priceMax

            if (result.isSuccess) {
                var list = result.getOrNull() ?: listOf()
                var filteredList = mutableListOf<Notebook>()
                repeat(list.size) { i ->
                    val notebook = list[i]
                    log("1st filter: $notebook")
                    val excludeTitle = if (excludeTitleFilter.isNotEmpty()) {
                        excludeTitleFilter.any { filter -> notebook.title.contains(filter, true) }
                    } else {
                        false
                    }
                    val excludeDescription = if (excludeDescriptionFilter.isNotEmpty()) {
                        excludeDescriptionFilter.any { filter -> notebook.description.contains(filter, true) }
                    } else {
                        false
                    }
                    val excludeByPrice = (notebook.price in priceMin..priceMax).not()
                    val condition = excludeTitle || excludeDescription || excludeByPrice
                    if (condition.not()) {
                        filteredList.add(notebook)
                    }
                    delay(1)
                }

                list = filteredList
                filteredList = mutableListOf()

                repeat(list.size) { i ->
                    val notebook = list[i]
                    log("2st filter: ${notebook.title} ")
                    val title = if (titleFilters.isNotEmpty()) {
                        titleFilters.any { filter -> notebook.title.contains(filter, true) }
                    } else {
                        true
                    }
                    val description = if (descriptionsFilter.isNotEmpty()) {
                        descriptionsFilter.any { filter -> notebook.description.contains(filter, true) }
                    } else {
                        true
                    }
                    val condition = when {
                        title.not() && descriptionsFilter.isEmpty() -> false
                        description.not() && titleFilters.isEmpty() -> false
                        else -> title || description
                    }
                    if (condition) {
                        filteredList.add(notebook)
                    }
                    delay(1)
                }

                list = filteredList
                filteredList = mutableListOf()

                repeat(list.size) { i ->
                    val notebook = list[i]
                    log("3st filter: $notebook")
                    val condition = try {
                        val notebookDetails = avitoRepository.getNotebookDetails(notebook).getOrThrow()
                        val details = if (detailsFilter.isNotEmpty()) {
                            detailsFilter.any { filter -> notebookDetails.details.contains(filter, true) }
                        } else {
                            true
                        }
                        val excludeDetails = if (excludeDetailsFilter.isNotEmpty()) {
                            excludeDetailsFilter.any { filter ->
                                !notebookDetails.details.contains(filter, true)
                            }
                        } else {
                            true
                        }
                        details && excludeDetails
                    } catch (e: NotebookDetailsParsingException) {
                        e.printStackTrace()
                        telegramMessenger.sendException(e, configs.telegramGroupId)
                        false
                    }
                    if (condition) {
                        filteredList.add(notebook)
                    }
                    delay(1)
                }

                repeat(filteredList.size) { i ->
                    val notebook = list[i]
                    log("4st filter: $notebook")
                    telegramMessenger.sendMessage(notebook, configs.telegramGroupId)
                }
            }
            log("last line of instruction run")
        } catch (e: Exception) {
            e.printStackTrace()
            telegramMessenger.sendException(e, configs.telegramGroupId)
        }
    }
}
