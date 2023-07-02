package data.source.local

import domain.models.Notebook
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.io.File

internal class NotebookHistoryLocalDataSourceTest {

    private val testNotebook1 = Notebook(
        1,
        "test url 1",
        "test title 1",
        10000,
        "test description 1"
    )
    private val testNotebook2 = Notebook(
        2,
        "test url 2",
        "test title 2",
        20000,
        "test description 2"
    )
    private val testNotebook3 = Notebook(
        3,
        "test url 3",
        "test title 3",
        30000,
        "test description 3"
    )

    companion object {

        private val notebookHistoryDataSource = NotebookHistoryLocalDataSource()
        private val tempDir = File(System.getProperty("java.io.tmpdir"))
        private val file = File("$tempDir\\notebook_history.txt")

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            notebookHistoryDataSource.clearHistory()
        }

        internal fun readFile(): List<String> {
            return file.readLines()
        }
    }

    @BeforeEach
    internal fun beforeEach() {
        notebookHistoryDataSource.clearHistory()
    }

    @Test
    fun recordNotebook() {
        notebookHistoryDataSource.recordNotebook(testNotebook1)
        assert(readFile() == listOf("1;10000"))
        notebookHistoryDataSource.recordNotebook(testNotebook2)
        assert(readFile() == listOf("2;20000", "1;10000"))
    }

    @Test
    fun isNotebookAlreadyChecked() {
        notebookHistoryDataSource.recordNotebook(testNotebook1)
        assert(notebookHistoryDataSource.isNotebookAlreadyChecked(testNotebook1))
        notebookHistoryDataSource.recordNotebook(testNotebook2)
        notebookHistoryDataSource.recordNotebook(testNotebook3)
        assert(notebookHistoryDataSource.isNotebookAlreadyChecked(testNotebook2))
        assert(notebookHistoryDataSource.isNotebookAlreadyChecked(testNotebook3))
    }

    @Test
    fun getPriceDifference() {
        notebookHistoryDataSource.recordNotebook(testNotebook1)
        notebookHistoryDataSource.recordNotebook(testNotebook2)
        notebookHistoryDataSource.recordNotebook(testNotebook3)
        assert(
            notebookHistoryDataSource.getPriceDifference(
                Notebook(
                    1,
                    "test url 1",
                    "test title 1",
                    10000,
                    "test description 1"
                )
            ) == 0
        )
        assert(
            notebookHistoryDataSource.getPriceDifference(
                Notebook(
                    1,
                    "test url 1",
                    "test title 1",
                    20000,
                    "test description 1"
                )
            ) > 0
        )
        assert(
            notebookHistoryDataSource.getPriceDifference(
                Notebook(
                    1,
                    "test url 1",
                    "test title 1",
                    2000,
                    "test description 1"
                )
            ) < 0
        )
        assert(
            notebookHistoryDataSource.getPriceDifference(
                Notebook(
                    2,
                    "test url 2",
                    "test title 2",
                    20000,
                    "test description 2"
                )
            ) == 0
        )
        assert(
            notebookHistoryDataSource.getPriceDifference(
                Notebook(
                    2,
                    "test url 2",
                    "test title 2",
                    21000,
                    "test description 2"
                )
            ) > 0
        )
        assert(
            notebookHistoryDataSource.getPriceDifference(
                Notebook(
                    2,
                    "test url 2",
                    "test title 2",
                    1000,
                    "test description 2"
                )
            ) < 0
        )
    }

    @Test
    fun clearHistory() {
        notebookHistoryDataSource.recordNotebook(testNotebook2)
        notebookHistoryDataSource.clearHistory()
        assert(file.exists().not())
    }
}