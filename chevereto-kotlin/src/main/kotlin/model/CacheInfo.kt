package model

/**
 * 缓存信息
 *
 *  @property url 链接
 *  @property name 名称
 *  @property parent 父级(默认为category的name)
 */
data class CacheInfo(val url: String, val name: String, val parent: String = "")