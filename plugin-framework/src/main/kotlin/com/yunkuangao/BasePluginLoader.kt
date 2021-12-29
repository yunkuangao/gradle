package com.yunkuangao

import com.yunkuangao.util.FileUtils.Companion.getJars
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

open class BasePluginLoader(
    private val pluginManager: PluginManager,
    private val pluginClasspath: PluginClasspath,
) : PluginLoader {

    override fun isApplicable(pluginPath: Path): Boolean {
        return Files.exists(pluginPath)
    }

    override fun loadPlugin(pluginPath: Path, pluginDescriptor: PluginDescriptor): ClassLoader {
        val pluginClassLoader = createPluginClassLoader(pluginPath, pluginDescriptor)
        loadClasses(pluginPath, pluginClassLoader)
        loadJars(pluginPath, pluginClassLoader)
        return pluginClassLoader
    }

    protected fun createPluginClassLoader(pluginPath: Path, pluginDescriptor: PluginDescriptor): PluginClassLoader {
        return PluginClassLoader(pluginManager, pluginDescriptor, javaClass.classLoader)
    }

    protected fun loadClasses(pluginPath: Path, pluginClassLoader: PluginClassLoader) {
        for (directory in pluginClasspath.getClassesDirectories()) {
            val file = pluginPath.resolve(directory).toFile()
            if (file.exists() && file.isDirectory) {
                pluginClassLoader.addFile(file)
            }
        }
    }

    protected fun loadJars(pluginPath: Path, pluginClassLoader: PluginClassLoader) {
        for (jarsDirectory in pluginClasspath.getJarsDirectories()) {
            val file = pluginPath.resolve(jarsDirectory)
            val jars: MutableList<File> = getJars(file)
            for (jar in jars) {
                pluginClassLoader.addFile(jar)
            }
        }
    }

}