package parse

import constant.cacheFileList
import constant.musicDirector
import constant.sep
import handler.chrome
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import model.CacheInfo
import model.CheveretoInfo
import model.CheveretoInfo.Companion.albums
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

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30 * 1000
        }
    }

    private val logger = KotlinLogging.logger {}

    fun categorys(): List<CacheInfo> {
        return if (cheveretoInfo.categorys.size > 0) cheveretoInfo.categorys else getCategorys()
    }

    fun images(): List<CacheInfo> {
        return if (cheveretoInfo.images.size > 0) cheveretoInfo.images else getImages()
    }

    fun download(): Boolean {
        return try {
            images().run { logger.info("chevereto download finish") }
            true
        } catch (e: Exception) {
            logger.error(e.message)
            logger.debug { e.printStackTrace() }
            false
        }
    }

    fun quit() = driver.quit()

    private fun getCategorys(): List<CacheInfo> {

        driver.get(cheveretoInfo.url.toString() + albums)

        return driver.findElements(By.className("list-item-desc-title-link"))
            .map { CacheInfo(it.getAttribute("href"), it.getAttribute("innerText")) }
            .also { cheveretoInfo.categorys.addAll(it) }
    }

    private fun getImages(): List<CacheInfo> {

        if (cheveretoInfo.categorys.size <= 0) getCategorys()

        for (category in cheveretoInfo.categorys) {
            driver.get(category.url)

            driver.findElements(By.className("jsly-loaded"))
                .map {
                    val name = it.getAttribute("alt")
                    val src = it.getAttribute("src")
                    val url = src.substring(0, src.lastIndexOf("/") + 1) + name
                    CacheInfo(url, name, category.name)
                }
                .also {
                    cheveretoInfo.images.addAll(it)
                    saveImage(it)
                }
        }
        return cheveretoInfo.images
    }

    private fun saveImage(images: List<CacheInfo>) {
        images.forEach {
            val filePath = musicDirector() + sep + cheveretoPath + sep + it.parent + sep + it.url.substring(it.url.lastIndexOf("/"))

            if (!cacheFiles.contains(it.name)) {
                runBlocking {
                    try {
                        val httpResponse: HttpResponse = retryIO(times = 3) {
                            client.get(it.url) {
                                onDownload { bytesSentTotal, contentLength ->
                                    logger.debug("Received $bytesSentTotal bytes from $contentLength")
                                }
                            }
                        }

                        writeFile(filePath, httpResponse.receive()).run { logger.info("A file ${it.name} saved to $filePath") }

                    } catch (e: Exception) {
                        logger.error(e.message)
                        logger.debug { e.printStackTrace() }
                    }
                }
            } else {
                logger.debug("file ${it.name} was saved to $filePath")
            }
        }
    }

    companion object {
        const val cheveretoPath = "chevereto"

        private lateinit var cacheFiles: List<String>

        init {
            updateCacheFiles()
        }

        fun updateCacheFiles() {
            cacheFiles = cacheFileList(musicDirector() + sep + cheveretoPath, mutableListOf())
        }
    }

}
