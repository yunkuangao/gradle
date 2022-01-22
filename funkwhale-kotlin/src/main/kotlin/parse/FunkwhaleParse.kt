package parse

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import constant.cacheFileList
import constant.musicDirector
import constant.sep
import handler.chrome
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import model.CacheInfo
import model.FunkwhaleInfo
import mu.KotlinLogging
import org.openqa.selenium.WebDriver
import util.createDirection
import util.retryIO
import util.validFileName
import util.writeFile

class FunkwhaleParse(
    private val funkwhaleInfo: FunkwhaleInfo,
    private val driver: WebDriver = chrome(),
) {

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30 * 1000
        }
        install(JsonFeature)
    }

    private val logger = KotlinLogging.logger {}

    fun albums(): List<CacheInfo> {
        return if (funkwhaleInfo.albums.size > 0) funkwhaleInfo.albums else getInfo(albumB = true).run { funkwhaleInfo.albums }
    }

    fun musics(): List<CacheInfo> {
        return if (funkwhaleInfo.musics.size > 0) funkwhaleInfo.musics else getInfo(musicB = true).run { funkwhaleInfo.musics }
    }

    fun download(): Boolean {
        return try {
            saveMusics().run { logger.info("funkwhale download finish") }
            true
        } catch (e: Exception) {
            logger.error(e.message)
            logger.debug { e.printStackTrace() }
            false
        }
    }

    fun quit() = driver.quit()

    private fun getInfo(
        albumB: Boolean = false,
        musicB: Boolean = false,
    ) {
        val url = funkwhaleInfo.url.toString() + "/rest/search3?u=${funkwhaleInfo.username}&p=${funkwhaleInfo.password}&f=json&songCount=1000&albumCount=1000"

        runBlocking {
            try {
                val searchResult: String = retryIO(times = 3) { client.get(url) }

                val json: JsonObject = Parser.default().parse(StringBuilder(searchResult)) as JsonObject

                // album list
                if (albumB) {
                    val albumList: List<JsonObject> = json.obj("subsonic-response")?.obj("searchResult3")?.array<JsonObject>("album")?.toList() ?: listOf()
                    funkwhaleInfo.albums.addAll(albumList.map {
                        val album = funkwhaleInfo.url.toString() + "/rest/getAlbum?u=${funkwhaleInfo.username}&p=${funkwhaleInfo.password}&f=json&id=${it.int("id")}"
                        CacheInfo(album, it.string("name") ?: "unknown")
                    })
                }

                // music list
                if (musicB) {
                    val musicList: List<JsonObject> = json.obj("subsonic-response")?.obj("searchResult3")?.array<JsonObject>("song")?.toList() ?: listOf()
                    funkwhaleInfo.musics.addAll(musicList.map {
                        val music = funkwhaleInfo.url.toString() + "/rest/stream?u=${funkwhaleInfo.username}&p=${funkwhaleInfo.password}&f=json&id=${it.int("id")}&format=mp3"
                        CacheInfo(music, it.string("title") ?: "unknown", it.string("artist") ?: "unknown", suffix = "mp3")
                    })
                }

            } catch (e: Exception) {
                logger.error(e.message)
                logger.debug { e.printStackTrace() }
            }
        }
    }

    private fun saveMusics() {
        if (funkwhaleInfo.musics.size <= 0) getInfo(musicB = true)
        funkwhaleInfo.musics.forEach {
            val filePath = musicDirector + sep + savePath + sep + validFileName(it.parent) + sep + validFileName(it.name) + if (it.suffix.isNotEmpty()) "." + it.suffix else ""
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
        const val savePath = "funkwhale"

        private lateinit var cacheFiles: List<String>

        init {
            updateCacheFiles()
        }

        fun updateCacheFiles() {
            createDirection(musicDirector + sep + savePath) // ensure music director exist
            cacheFiles = cacheFileList(musicDirector + sep + savePath, mutableListOf())
        }
    }

}
