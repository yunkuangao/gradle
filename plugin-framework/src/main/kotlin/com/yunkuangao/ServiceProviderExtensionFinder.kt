package com.yunkuangao

import com.yunkuangao.processor.ExtensionStorage
import com.yunkuangao.processor.ServiceProviderExtensionStorage
import com.yunkuangao.util.FileUtils.Companion.closePath
import com.yunkuangao.util.FileUtils.Companion.getPath
import mu.KotlinLogging
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

open class ServiceProviderExtensionFinder(
    pluginManager: PluginManager,
) : AbstractExtensionFinder(pluginManager) {

    private val logger = KotlinLogging.logger {}

    override fun readClasspathStorages(): MutableMap<String, MutableSet<String>> {
        logger.debug("Reading extensions storages from classpath")
        val result: MutableMap<String, MutableSet<String>> = LinkedHashMap()
        val bucket: MutableSet<String> = HashSet()
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
        } catch (e: URISyntaxException) {
            logger.error(e.message, e)
        }
        return result
    }

    override fun readPluginsStorages(): MutableMap<String, MutableSet<String>> {
        logger.debug("Reading extensions storages from plugins")
        val result: MutableMap<String, MutableSet<String>> = mutableMapOf()
        val plugins: MutableList<PluginWrapper> = pluginManager.plugins()
        for (plugin in plugins) {
            val pluginId: String = plugin.descriptor.getPluginId()
            logger.debug("Reading extensions storages for plugin '{}'", pluginId)
            val bucket: MutableSet<String> = mutableSetOf()
            try {
                val urls = (plugin.pluginClassLoader as PluginClassLoader).findResources(EXTENSIONS_RESOURCE)
                if (urls.hasMoreElements()) {
                    collectExtensions(urls, bucket)
                } else {
                    logger.debug("Cannot find '{}'", EXTENSIONS_RESOURCE)
                }
                debugExtensions(bucket)
                result[pluginId] = bucket
            } catch (e: IOException) {
                logger.error(e.message, e)
            } catch (e: URISyntaxException) {
                logger.error(e.message, e)
            }
        }
        return result
    }

    private fun collectExtensions(urls: Enumeration<URL>, bucket: MutableSet<String>) {
        while (urls.hasMoreElements()) {
            val url = urls.nextElement()
            logger.debug("Read '{}'", url.file)
            collectExtensions(url, bucket)
        }
    }

    private fun collectExtensions(url: URL, bucket: MutableSet<String>) {
        val extensionPath: Path = if (url.toURI().scheme == "jar") {
            getPath(url.toURI(), EXTENSIONS_RESOURCE)
        } else {
            Paths.get(url.toURI())
        }
        try {
            bucket.addAll(readExtensions(extensionPath))
        } finally {
            closePath(extensionPath)
        }
    }

    private fun readExtensions(extensionPath: Path): MutableSet<String> {
        val result: MutableSet<String> = mutableSetOf()
        Files.walkFileTree(extensionPath, emptySet(), 1, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                logger.debug("Read '{}'", file)
                Files.newBufferedReader(file, StandardCharsets.UTF_8).use { reader -> ExtensionStorage.read(reader, result) }
                return FileVisitResult.CONTINUE
            }
        })
        return result
    }

    companion object {
        val EXTENSIONS_RESOURCE: String = ServiceProviderExtensionStorage.EXTENSIONS_RESOURCE
    }
}