package com.yunkuangao

import com.yunkuangao.processor.ExtensionStorage
import com.yunkuangao.processor.LegacyExtensionStorage
import mu.KotlinLogging
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

open class LegacyExtensionFinder(
    pluginManager: PluginManager,
) : AbstractExtensionFinder(pluginManager) {

    private val logger = KotlinLogging.logger {}

    override fun readClasspathStorages(): MutableMap<String, MutableSet<String>> {
        logger.debug("Reading extensions storages from classpath")
        val result: MutableMap<String, MutableSet<String>> = LinkedHashMap()
        val bucket: MutableSet<String> = mutableSetOf()
        try {
            val urls = javaClass.classLoader.getResources(EXTENSIONS_RESOURCE)
            if (urls.hasMoreElements()) {
                collectExtensions(urls, bucket)
            } else {
                logger.debug("Cannot find '{}'", EXTENSIONS_RESOURCE)
            }
            debugExtensions(bucket)
            result["null"] = bucket
        } catch (e: IOException) {
            logger.error(e.message, e)
        }
        return result
    }

    override fun readPluginsStorages(): MutableMap<String, MutableSet<String>> {
        logger.debug("Reading extensions storages from plugins")
        val result: MutableMap<String, MutableSet<String>> = LinkedHashMap()
        val plugins: MutableList<PluginWrapper> = pluginManager.plugins()
        for (plugin in plugins) {
            val pluginId: String = plugin.descriptor.getPluginId()
            logger.debug("Reading extensions storage from plugin '{}'", pluginId)
            val bucket: MutableSet<String> = mutableSetOf()
            try {
                logger.debug("Read '{}'", EXTENSIONS_RESOURCE)
                val pluginClassLoader: ClassLoader = plugin.pluginClassLoader
                pluginClassLoader.getResourceAsStream(EXTENSIONS_RESOURCE).use { resourceStream ->
                    if (resourceStream == null) {
                        logger.debug("Cannot find '{}'", EXTENSIONS_RESOURCE)
                    } else {
                        collectExtensions(resourceStream, bucket)
                    }
                }
                debugExtensions(bucket)
                result[pluginId] = bucket
            } catch (e: IOException) {
                logger.error(e.message, e)
            }
        }
        return result
    }

    private fun collectExtensions(urls: Enumeration<URL>, bucket: MutableSet<String>) {
        while (urls.hasMoreElements()) {
            val url = urls.nextElement()
            logger.debug("Read '{}'", url.file)
            collectExtensions(url.openStream(), bucket)
        }
    }

    private fun collectExtensions(inputStream: InputStream, bucket: MutableSet<String>) {
        InputStreamReader(inputStream, StandardCharsets.UTF_8).use { reader -> ExtensionStorage.read(reader, bucket) }
    }

    companion object {
        const val EXTENSIONS_RESOURCE: String = LegacyExtensionStorage.EXTENSIONS_RESOURCE
    }

}