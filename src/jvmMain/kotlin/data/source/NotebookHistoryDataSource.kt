package data.source

import domain.models.Notebook

interface NotebookHistoryDataSource {

    fun recordNotebook(notebook: Notebook)

    fun isNotebookAlreadyChecked(notebook: Notebook): Boolean

    /**
    * function returns a difference between record and a notebook from function parameter.
     * if result bigger than 0 notebook from record cheaper. Else more expensive.
     * return 0 if there are no difference
     */
    fun getPriceDifference(notebook: Notebook): Int

    fun overrideRecord(notebook: Notebook)

    fun clearHistory()
}