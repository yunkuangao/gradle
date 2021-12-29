package com.yunkuangao

import java.nio.file.Path

interface PluginRepository {

    fun getPluginPaths(): MutableList<Path>

    fun deletePluginPath(pluginPath: Path): Boolean
}