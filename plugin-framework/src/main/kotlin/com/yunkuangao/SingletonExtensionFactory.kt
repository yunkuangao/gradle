package com.yunkuangao

import java.util.*

class SingletonExtensionFactory(
    vararg extensionClassNames: String,
) : DefaultExtensionFactory() {

    private val extensionClassNames: MutableList<String>
    private var cache: MutableMap<ClassLoader, MutableMap<String, Any>>

    init {
        this.extensionClassNames = mutableListOf(*extensionClassNames)
        cache = WeakHashMap() // simple cache implementation
    }

    override fun <T> create(extensionClass: Class<T>): T {
        val extensionClassName = extensionClass.name
        val extensionClassLoader = extensionClass.classLoader
        if (!cache.containsKey(extensionClassLoader)) {
            cache[extensionClassLoader] = HashMap()
        }
        val classLoaderBucket = cache[extensionClassLoader]
        if (classLoaderBucket!!.containsKey(extensionClassName)) {
                return classLoaderBucket[extensionClassName] as T
        }
        val extension = super.create(extensionClass)
        if (extensionClassNames.isEmpty() || extensionClassNames.contains(extensionClassName)) {
            classLoaderBucket[extensionClassName] = extension!!
        }
        return extension
    }
}