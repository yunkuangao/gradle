package com.yunkuangao

import java.nio.file.Path

interface PluginDescriptorFinder {

    fun isApplicable(pluginPath: Path): Boolean

    fun find(pluginPath: Path): PluginDescriptor
}