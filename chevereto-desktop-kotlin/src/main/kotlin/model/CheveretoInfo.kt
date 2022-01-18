package model

import java.net.URL

class CheveretoInfo(
    val url: URL,
) {
    val categorys: MutableList<CacheInfo> = mutableListOf()
    val images: MutableList<CacheInfo> = mutableListOf()


    constructor(url: String) : this(URL(url))
}