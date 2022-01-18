package parse

import constant.cheveretoPath
import constant.musicDirector
import handler.chrome
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import model.CacheInfo
import model.CheveretoInfo
import mu.KotlinLogging
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import util.retryIO
import util.writeFile
import java.io.File

class CheveretoParse(
    private val cheveretoInfo: CheveretoInfo,
    private val driver: WebDriver = chrome(),
) {

    private val logger = KotlinLogging.logger {}

    fun categorys(): List<CacheInfo> {
        return if (cheveretoInfo.categorys.size > 0) cheveretoInfo.categorys else getCategorys()
    }

    fun images(): List<CacheInfo> {
        return if (cheveretoInfo.images.size > 0) cheveretoInfo.images else getImages()
    }

    fun download(): Boolean {
        return try {
            images().run { logger.info("下载成功") }
            true
        } catch (e: Exception) {
            logger.error(e.message)
            logger.debug { e.printStackTrace() }
            false
        }
    }

    fun quit() = driver.quit()

    private fun getCategorys(): MutableList<CacheInfo> {
        driver.get(cheveretoInfo.url.toString())
        return driver.findElements(By.tagName("a"))
            .filter { "category-name" == it.getAttribute("data-content") && it.getAttribute("href").contains("category") }
            .map { CacheInfo(it.getAttribute("href"), it.getAttribute("innerText")) }
            .toMutableList()
            .also { cheveretoInfo.categorys.addAll(it) }
    }

    private fun getImages(): MutableList<CacheInfo> {

        if (cheveretoInfo.categorys.size <= 0) getCategorys()

        for (category in cheveretoInfo.categorys) {
            driver.get(category.url)

            val imageUrls = driver.findElements(By.tagName("a"))
                .filter { "image-link" == it.getAttribute("data-content") }
                .map { CacheInfo(it.getAttribute("href"), it.getAttribute("innerText")) }

            for (imageUrl in imageUrls) {
                driver.get(imageUrl.url)

                val imgUrl = driver.findElements(By.tagName("img"))
                    .filter { imageUrl.name == it.getAttribute("alt") }
                    .map { it.getAttribute("src") }[0]

                runBlocking {
                    val client = HttpClient {
                        install(HttpTimeout) {
                            requestTimeoutMillis = 10 * 1000
                        }
                    }
                    try {
                        val httpResponse: HttpResponse = retryIO(times = 3) {
                            client.get(imgUrl) {
                                onDownload { bytesSentTotal, contentLength ->
                                    logger.debug("Received $bytesSentTotal bytes from $contentLength")
                                }
                            }
                        }

                        val filePath =
                            musicDirector() + File.separator + cheveretoPath + File.separator + category.name + File.separator + imgUrl.substring(imgUrl.lastIndexOf("/"))
                        writeFile(filePath, httpResponse.receive()).run { logger.info("A file saved to $filePath") }

                    } catch (e: Exception) {
                        logger.error(e.message)
                        logger.debug { e.printStackTrace() }
                    }
                }
            }
        }
        return cheveretoInfo.images
    }
}

