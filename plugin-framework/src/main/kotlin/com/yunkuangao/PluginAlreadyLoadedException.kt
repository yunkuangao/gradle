package com.yunkuangao

import java.nio.file.Path

class PluginAlreadyLoadedException(
    private val pluginId: String,
    private val pluginPath: Path,
) : PluginRuntimeException("Plugin '{}' already loaded with id '{}'", pluginPath, pluginId) {

    fun pluginId(): String {
        return pluginId
    }

    fun pluginPath(): Path {
        return pluginPath
    }
}