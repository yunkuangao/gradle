package parse

import file.cacheFileList
import file.pictureDirectory
import file.sep
import file.writeFile
import handler.chrome
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
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

class CheveretoParse(
    private val cheveretoInfo: CheveretoInfo,
    private val driver: WebDriver = chrome(),
    private val proxys: String = "",
) : CommonParse {

    private val client = HttpClient {
        engine {
            if (proxys.isNotEmpty()) proxy = ProxyBuilder.http(proxys)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30 * 1000
        }
    }

    private val logger = KotlinLogging.logger {}

    override fun categoryList(): List<CacheInfo> {
        return if (cheveretoInfo.categorys.size > 0) cheveretoInfo.categorys else getCategorys()
    }

    override fun fileList(): List<CacheInfo> {
        return if (cheveretoInfo.images.size > 0) cheveretoInfo.images else getImages()
    }

    override fun download(): Boolean {
        return try {
            fileList().run { logger.info("chevereto下载完成") }
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
            .map {
                CacheInfo(
                    it.getAttribute("href"),
                    it.getAttribute("innerText")
                ).also { info -> logger.info("找到${info.name}集锦") }
            }
            .also {
                cheveretoInfo.categorys.addAll(it)
            }
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
            val filePath =
                pictureDirectory + sep + cheveretoPath + sep + it.parent + sep + it.url.substring(it.url.lastIndexOf("/"))

            if (!cacheFiles.contains(it.name)) {
                runBlocking {
                    try {
                        val httpResponse: HttpResponse = retryIO(times = 3) {
                            client.get(it.url) {
                                onDownload { bytesSentTotal, contentLength ->
                                    logger.debug("接收$bytesSentTotal 字节 从 $contentLength")
                                }
                            }
                        }

                        writeFile(
                            filePath,
                            httpResponse.readBytes()
                        ).run { logger.info("保存文件${it.name} 到$filePath") }

                    } catch (e: Exception) {
                        logger.error(e.message)
                        logger.debug { e.printStackTrace() }
                    }
                }
            } else {
                logger.debug("文件 ${it.name} 已存在于$filePath")
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
            cacheFiles = cacheFileList(pictureDirectory + sep + cheveretoPath, mutableListOf())
        }
    }

}

