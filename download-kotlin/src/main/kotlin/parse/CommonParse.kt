package parse

import model.CacheInfo

interface CommonParse {

    fun categoryList(): List<CacheInfo>

    fun fileList(): List<CacheInfo>

    fun download(): Boolean

}
