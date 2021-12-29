package com.yunkuangao

import mu.KotlinLogging
import java.nio.file.Path

class CompoundPluginDescriptorFinder : PluginDescriptorFinder {

    private val logger = KotlinLogging.logger {}

    private val finders: MutableList<PluginDescriptorFinder> = mutableListOf()

    fun add(finder: PluginDescriptorFinder): CompoundPluginDescriptorFinder {
        finders.add(finder)
        return this
    }

    fun size(): Int {
        return finders.size
    }

    override fun isApplicable(pluginPath: Path): Boolean {
        for (finder in finders) {
            if (finder.isApplicable(pluginPath)) {
                return true
            }
        }
        return false
    }

    override fun find(pluginPath: Path): PluginDescriptor {
        for (finder in finders) {
            if (finder.isApplicable(pluginPath)) {
                logger.debug { "'$finder' is applicable for plugin '$pluginPath'" }
                try {
                    return finder.find(pluginPath)
                } catch (e: Exception) {
                    if (finders.indexOf(finder) == finders.size - 1) {
                        logger.error(e.message, e)
                    } else {
                        logger.debug(e.message)
                        logger.debug("Try to continue with the next finder")
                    }
                }
            } else {
                logger.debug("'{}' is not applicable for plugin '{}'", finder, pluginPath)
            }
        }
        throw PluginRuntimeException("No PluginDescriptorFinder for plugin '{}'", pluginPath)
    }

}