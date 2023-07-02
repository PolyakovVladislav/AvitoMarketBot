package data.source.remote

import data.source.NotebooksDataSource
import domain.exceptions.NotebookDetailsParsingException
import domain.models.LocationSuggestion
import domain.models.Notebook
import domain.models.NotebookDetails
import io.github.bonigarcia.wdm.managers.ChromeDriverManager
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

class NotebookRemoteDataSource : NotebooksDataSource {

    private val webDriver by lazy(LazyThreadSafetyMode.NONE) {
        val options = ChromeOptions()
        options.addArguments("--log-level=3", "--headless")
        ChromeDriverManager.chromiumdriver().setup()
        ChromeDriver(options)
    }

    override suspend fun getList(location: String): List<Notebook> {
        val urlLocation = location.ifBlank { "rossiya" }
        webDriver.get("https://www.avito.ru/$urlLocation/noutbuki?s=104")
        val lots = webDriver.findElements(
            By.xpath(
                "//div[@class=\"items-items-kAJAg\"]/div[@data-marker=\"item\"]"
            )
        )
        return lots.mapIndexed { index, lot ->
            val id = lot.findElement(
                By.xpath(
                    "//div[@class=\"items-items-kAJAg\"]" +
                            "/div[@data-marker=\"item\"][${index + 1}]"
                )
            ).getAttribute("data-item-id").toLong()
            val url = lot.findElement(
                By.xpath(
                    "//div[@class=\"items-items-kAJAg\"]" +
                            "/div[@data-marker=\"item\"][${index + 1}]" +
                            "//a[contains(@href,\"\") and contains(@class,\"link-link-MbQDP\")]"
                )
            ).getAttribute("href")

            val title = lot.findElement(
                By.xpath(
                    "//div[@class=\"items-items-kAJAg\"]" +
                            "/div[@data-marker=\"item\"][${index + 1}]" +
                            "//a[contains(@href,\"\") and contains(@class,\"link-link-MbQDP\")]"
                )
            ).getAttribute("title")

            val price = lot.findElement(
                By.xpath(
                    "//div[@class=\"items-items-kAJAg\"]" +
                            "/div[@data-marker=\"item\"][${index + 1}]" +
                            "//meta[@itemprop=\"price\"]"
                )
            ).getAttribute("content").toInt()

            val description = lot.findElement(
                By.xpath(
                    "//div[@class=\"items-items-kAJAg\"]" +
                            "/div[@data-marker=\"item\"][${index + 1}]" +
                            "//div[@class=\"iva-item-text-Ge6dR iva-item-description-FDgK4 text-text-LurtD text-size-s-BxGpL\"]"
                )
            ).text

            Notebook(
                id,
                url,
                title ?: "",
                price,
                description ?: ""
            )
        }
    }


    override suspend fun getNotebookDetails(notebook: Notebook): NotebookDetails {
        try {
            webDriver.get(notebook.url)
            val title = webDriver.findElement(
                By.xpath("//span[@itemprop=\"name\" and @class=\"title-info-title-text\"]")
            ).text
            val details = webDriver.findElement(
                By.xpath("//ul[@class=\"params-paramsList-zLpAu\"]")
            ).text
            var moreDetails = ""
            try {
                val moreDetailsUrl = webDriver.findElement(By.xpath("//div[@class=\"params-specification-2x7V3\"]/a"))
                    .getAttribute("href")
                webDriver.get(moreDetailsUrl)
                moreDetails = webDriver.findElement(By.xpath("//div[@class=\"styles-narrowBlocks-FRFsk\"]")).text
            } catch (_: Exception) {
            }
            return NotebookDetails(
                notebook.id,
                title,
                notebook.price,
                "$details $moreDetails",
                notebook.description
            )
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            throw NotebookDetailsParsingException(notebook, e.message.toString(), webDriver.pageSource)
        }
    }

    override suspend fun getLocationForInput(input: String): List<LocationSuggestion> {
        val locationWindow: WebElement? = try {
            webDriver
                .findElement(By.xpath("//div[@class=\"popup-content-sgjA0 popup-padding-old-SCOpn\"]"))
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            null
        }
        if (locationWindow == null) {
            webDriver.get("https://www.avito.ru/rossiya/noutbuki")
            webDriver
                .findElement(By.xpath("//div[@class=\"main-select-JJyaZ main-location-XUs1_\"]"))
                .click()
        }
        val preLoadedSuggestions = getLocationSuggestionList()
        webDriver.findElement(By.xpath("//input[@class=\"suggest-input-rORJM\"]")).sendKeys(input)
        var counter = 0
        var suggestionList = getLocationSuggestionList()
        while (
            preLoadedSuggestions == suggestionList &&
            counter < 20
        ) {
            counter++
            delay(50)
            suggestionList = getLocationSuggestionList()
        }
        return suggestionList
    }

    override fun getLocationPartForUrl(locationSuggestion: LocationSuggestion): String {
        webDriver.findElement(By.xpath("//li[@data-marker=\"suggest(${locationSuggestion.id})\"]")).click()
        webDriver.findElement(By.xpath("//button[@data-marker=\"popup-location/save-button\"]")).click()
        val res = webDriver.currentUrl.split("/")
        return res[3]
    }

    override suspend fun closeWebDriver() {
        try {
            webDriver.quit()
            webDriver.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLocationSuggestionList(): List<LocationSuggestion> {
        return List(webDriver.findElements(By.xpath("//li[contains(@data-marker,\"suggest\")]")).size) { index ->
            val text = webDriver.findElement(
                By.xpath(
                    "//li[contains(@data-marker,\"suggest\")][${index + 1}]" +
                            "//span[@class=\"suggest-suggest_content-_LYs8\"]"
                )
            ).text
            LocationSuggestion(text, index + 1)
        }
    }
}