package data.source.local

import data.source.NotebookHistoryDataSource
import domain.models.Notebook
import java.io.File

class NotebookHistoryLocalDataSource: NotebookHistoryDataSource {

    private val tempDir = File(System.getProperty("java.io.tmpdir"))
    private val file = File("$tempDir\\notebook_history.txt")

    override fun recordNotebook(notebook: Notebook) {
                if (file.exists().not()) {
            file.createNewFile()
        }
        val records = file.readLines().toMutableList()
        records.add(0, "${notebook.id};${notebook.price}")
        if (records.size > 250) {
            writeList(records.subList(0, 250))
        } else {
            writeList(records)
        }
    }

    override fun isNotebookAlreadyChecked(notebook: Notebook): Boolean {
                return if (file.exists().not()) {
            false
        } else {
            val records = file.readLines()
                        val result = records.any { record ->
                                record.split(";").first().toLong() == notebook.id
            }
                        result
        }
    }

    override fun getPriceDifference(notebook: Notebook): Int {
                if (file.exists().not()) {
            return -1
        }
        val records = file.readLines()
        val record = records.find { it.split(";").first().toLong() == notebook.id } ?: return -1
        return notebook.price - record.split(";").last().toInt()
    }

    override fun overrideRecord(notebook: Notebook) {
                if (file.exists()) {
            val records = file.readLines()
            val equalRecord = records.find { record -> record.split(";").first().toLong() == notebook.id }
            equalRecord.let { record ->
                val newList = records.toMutableList()
                newList[newList.indexOf(record)] = "${notebook.id};${notebook.price}"
                writeList(newList)
            }
        }
    }

    override fun clearHistory() {
        file.delete()
    }

    private fun writeList(list: List<String>) {
                val writer = file.bufferedWriter()
        writer.flush()
        list.forEachIndexed { index, string ->
            writer.write(string)
            if (index != list.size - 1) {
                writer.newLine()
            }
        }
        writer.close()
    }
}