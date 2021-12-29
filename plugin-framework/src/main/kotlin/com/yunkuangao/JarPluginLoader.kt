package com.yunkuangao

import com.yunkuangao.util.FileUtils.Companion.isJarFile
import java.nio.file.Files
import java.nio.file.Path

class JarPluginLoader(pluginManager: PluginManager) : PluginLoader {
    protected lateinit var pluginManager: PluginManager

    override fun isApplicable(pluginPath: Path): Boolean {
        return Files.exists(pluginPath) && isJarFile(pluginPath)
    }

    override fun loadPlugin(pluginPath: Path, pluginDescriptor: PluginDescriptor): ClassLoader {
        val pluginClassLoader = PluginClassLoader(pluginManager, pluginDescriptor, javaClass.classLoader)
        pluginClassLoader.addFile(pluginPath.toFile())
        return pluginClassLoader
    }

}