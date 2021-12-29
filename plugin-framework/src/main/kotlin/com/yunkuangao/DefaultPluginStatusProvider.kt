package com.yunkuangao

import com.yunkuangao.util.FileUtils.Companion.readLines
import com.yunkuangao.util.FileUtils.Companion.writeLines
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class DefaultPluginStatusProvider(private val pluginsRoot: Path) : PluginStatusProvider {

    private val logger = KotlinLogging.logger {}

    private lateinit var enabledPlugins: MutableList<String>
    private lateinit var disabledPlugins: MutableList<String>

    init {
        try {
            enabledPlugins = readLines(getEnabledFilePath(), true)
            logger.info("Enabled plugins: {}", enabledPlugins)

            disabledPlugins = readLines(getDisabledFilePath(), true)
            logger.info("Disabled plugins: {}", disabledPlugins)
        } catch (e: IOException) {
            logger.error(e.message, e)
        }
    }

    override fun isPluginDisabled(pluginId: String): Boolean {
        return if (disabledPlugins.contains(pluginId)) {
            true
        } else enabledPlugins.isNotEmpty() && !enabledPlugins.contains(pluginId)
    }

    override fun disablePlugin(pluginId: String) {
        if (isPluginDisabled(pluginId)) {
            return
        }
        if (Files.exists(getEnabledFilePath())) {
            enabledPlugins.remove(pluginId)
            try {
                writeLines(enabledPlugins, getEnabledFilePath())
            } catch (e: IOException) {
                throw PluginRuntimeException(e)
            }
        } else {
            disabledPlugins.add(pluginId)
            try {
                writeLines(disabledPlugins, getDisabledFilePath())
            } catch (e: IOException) {
                throw PluginRuntimeException(e)
            }
        }
    }

    override fun enablePlugin(pluginId: String) {
        if (!isPluginDisabled(pluginId)) {
            // do nothing
            return
        }
        if (Files.exists(getEnabledFilePath())) {
            enabledPlugins.add(pluginId)
            try {
                writeLines(enabledPlugins, getEnabledFilePath())
            } catch (e: IOException) {
                throw PluginRuntimeException(e)
            }
        } else {
            disabledPlugins.remove(pluginId)
            try {
                writeLines(disabledPlugins, getDisabledFilePath())
            } catch (e: IOException) {
                throw PluginRuntimeException(e)
            }
        }
    }

    fun getEnabledFilePath(): Path {
        return getEnabledFilePath(pluginsRoot)
    }

    fun getDisabledFilePath(): Path {
        return getDisabledFilePath(pluginsRoot)
    }

    companion object{
        fun getEnabledFilePath(pluginsRoot: Path): Path {
            return pluginsRoot.resolve("enabled.txt")
        }

        fun getDisabledFilePath(pluginsRoot: Path): Path {
            return pluginsRoot.resolve("disabled.txt")
        }
    }

}