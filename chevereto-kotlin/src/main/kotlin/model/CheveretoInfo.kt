package model

import java.net.URL

class CheveretoInfo(
    override val url: URL,
) : CommonInfo(url = url) {
    val categorys: MutableList<CacheInfo> = mutableListOf()
    val images: MutableList<CacheInfo> = mutableListOf()

    constructor(url: String) : this(URL(url))

    companion object {
        const val albums = "/me/albums"
    }
}