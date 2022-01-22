package model

import java.net.URL

class FunkwhaleInfo(
    val url: URL,
    val username: String = "",
    val password: String = "",
) {
    val albums: MutableList<CacheInfo> = mutableListOf()
    val musics: MutableList<CacheInfo> = mutableListOf()

    constructor(url: String, username: String, password: String) : this(URL(url), username, password)

    companion object {
        const val path = ""
    }
}