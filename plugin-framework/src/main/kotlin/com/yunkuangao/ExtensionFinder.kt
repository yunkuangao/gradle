package com.yunkuangao

interface ExtensionFinder {

    fun <T> find(type: Class<T>): MutableList<ExtensionWrapper<T>>

    fun <T> find(type: Class<T>, pluginId: String): MutableList<ExtensionWrapper<T>>

    fun <T> find(pluginId: String): MutableList<ExtensionWrapper<T>>

    fun findClassNames(pluginId: String): MutableSet<String>

}