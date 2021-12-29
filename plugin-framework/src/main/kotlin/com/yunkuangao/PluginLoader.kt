package com.yunkuangao

import java.nio.file.Path

interface PluginLoader {
    fun isApplicable(pluginPath: Path): Boolean

    fun loadPlugin(pluginPath: Path, pluginDescriptor: PluginDescriptor): ClassLoader
}