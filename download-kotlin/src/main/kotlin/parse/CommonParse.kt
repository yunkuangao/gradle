package parse

import model.CacheInfo

interface CommonParse {

    fun category(): List<CacheInfo>

    fun files(): List<CacheInfo>

    fun download(): Boolean

}
