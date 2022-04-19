package parse

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import file.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import model.CacheInfo
import model.FunkwhaleInfo
import mu.KotlinLogging
import util.retryIO

class FunkwhaleParse(
    private val funkwhaleInfo: FunkwhaleInfo,
) : CommonParse {

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30 * 1000
        }
        install(ContentNegotiation)
    }

    private val logger = KotlinLogging.logger {}

    override fun categoryList(): List<CacheInfo> {
        return if (funkwhaleInfo.albums.size > 0) funkwhaleInfo.albums else getInfo(albumB = true).run { funkwhaleInfo.albums }
    }

    override fun fileList(): List<CacheInfo> {
        return if (funkwhaleInfo.musics.size > 0) funkwhaleInfo.musics else getInfo(musicB = true).run { funkwhaleInfo.musics }
    }

    override fun download(): Boolean {
        return try {
            saveMusics().run { logger.info("funkwhale download finish") }
            true
        } catch (e: Exception) {
            logger.error(e.message)
            logger.debug { e.printStackTrace() }
            false
        }
    }

    private fun getInfo(
        albumB: Boolean = false,
        musicB: Boolean = false,
    ) {
        val url =
            funkwhaleInfo.url.toString() + "/rest/search3?u=${funkwhaleInfo.username}&p=${funkwhaleInfo.password}&f=json&songCount=1000&albumCount=1000"

        runBlocking {
            try {
                val searchResult: String = retryIO(times = 3) { client.get(url).bodyAsText() }

                val json: JsonObject = Parser.default().parse(StringBuilder(searchResult)) as JsonObject

                // album list
                if (albumB) {
                    val albumList: List<JsonObject> =
                        json.obj("subsonic-response")?.obj("searchResult3")?.array<JsonObject>("album")?.toList()
                            ?: listOf()
                    funkwhaleInfo.albums.addAll(albumList.map {
                        val album =
                            funkwhaleInfo.url.toString() + "/rest/getAlbum?u=${funkwhaleInfo.username}&p=${funkwhaleInfo.password}&f=json&id=${
                                it.int("id")
                            }"
                        CacheInfo(album, it.string("name") ?: "unknown")
                    })
                }

                // music list
                if (musicB) {
                    val musicList: List<JsonObject> =
                        json.obj("subsonic-response")?.obj("searchResult3")?.array<JsonObject>("song")?.toList()
                            ?: listOf()
                    funkwhaleInfo.musics.addAll(musicList.map {
                        val music =
                            funkwhaleInfo.url.toString() + "/rest/stream?u=${funkwhaleInfo.username}&p=${funkwhaleInfo.password}&f=json&id=${
                                it.int("id")
                            }&format=mp3"
                        CacheInfo(
                            music,
                            it.string("title") ?: "unknown",
                            it.string("artist") ?: "unknown",
                            suffix = "mp3"
                        )
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
            val filePath =
                musicDirectory + sep + savePath + sep + validFileName(it.parent) + sep + validFileName(it.name) + if (it.suffix.isNotEmpty()) "." + it.suffix else ""
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
                        writeFile(
                            filePath,
                            httpResponse.readBytes()
                        ).run { logger.info("A file ${it.name} saved to $filePath") }
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
            cacheFiles = cacheFileList(musicDirectory + sep + savePath, mutableListOf())
        }
    }

}
