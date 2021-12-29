package com.yunkuangao

import java.nio.file.Files
import java.nio.file.Path

class DefaultPluginLoader(
    pluginManager: PluginManager,
) : BasePluginLoader(pluginManager, DefaultPluginClasspath()) {

    override fun isApplicable(pluginPath: Path): Boolean {
        return super.isApplicable(pluginPath) && Files.isDirectory(pluginPath)
    }

}