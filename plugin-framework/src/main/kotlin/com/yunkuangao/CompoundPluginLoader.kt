package com.yunkuangao

import mu.KotlinLogging
import java.nio.file.Path
import java.util.function.BooleanSupplier

class CompoundPluginLoader: PluginLoader {

    private val logger = KotlinLogging.logger {}

    private val loaders: MutableList<PluginLoader> = mutableListOf()

    fun add(loader: PluginLoader): CompoundPluginLoader {
        loaders.add(loader)
        return this
    }

    /**
     * Add a [PluginLoader] only if the `condition` is satisfied.
     *
     * @param loader
     * @param condition
     * @return
     */
    fun add(loader: PluginLoader, condition: BooleanSupplier): CompoundPluginLoader {
        return if (condition.asBoolean) {
            add(loader)
        } else this
    }

    fun size(): Int {
        return loaders.size
    }

    override fun isApplicable(pluginPath: Path): Boolean {
        for (loader in loaders) {
            if (loader.isApplicable(pluginPath)) {
                return true
            }
        }
        return false
    }

    override fun loadPlugin(pluginPath: Path, pluginDescriptor: PluginDescriptor): ClassLoader {
        for (loader in loaders) {
            if (loader.isApplicable(pluginPath)) {
                logger.debug("'{}' is applicable for plugin '{}'", loader, pluginPath)
                try {
                    return loader.loadPlugin(pluginPath, pluginDescriptor)
                } catch (e: Exception) {
                    // log the exception and continue with the next loader
                    logger.error(e.message)
                }
            } else {
                logger.debug("'{}' is not applicable for plugin '{}'", loader, pluginPath)
            }
        }
        throw RuntimeException("No PluginLoader for plugin '$pluginPath' and descriptor '$pluginDescriptor'")
    }

}