package com.yunkuangao

import java.nio.file.Path

interface PluginManager {

    fun plugins(): MutableList<PluginWrapper>

    fun plugins(pluginState: PluginState): MutableList<PluginWrapper>

    fun plugin(pluginId: String): PluginWrapper

    fun resolvedPlugins(): MutableList<PluginWrapper>

    fun unresolvedPlugins(): MutableList<PluginWrapper>

    fun startedPlugins(): MutableList<PluginWrapper>

    fun loadPlugins()

    fun loadPlugin(pluginPath: Path): String

    fun startPlugins()

    fun startPlugin(pluginId: String): PluginState

    fun stopPlugins()

    fun stopPlugin(pluginId: String): PluginState

    fun unloadPlugins()

    fun unloadPlugin(pluginId: String): Boolean

    fun disablePlugin(pluginId: String): Boolean

    fun enablePlugin(pluginId: String): Boolean

    fun deletePlugin(pluginId: String): Boolean

    fun pluginClassLoader(pluginId: String): ClassLoader

    fun <T> extensionClasses(pluginId: String): MutableList<Class<T>>

    fun <T> extensionClasses(type: Class<T>): MutableList<Class<T>>

    fun <T> extensionClasses(type: Class<T>, pluginId: String): MutableList<Class<T>>

    fun <T> extensions(type: Class<T>): MutableList<T>

    fun <T> extensions(type: Class<T>, pluginId: String): MutableList<T>

    fun <T> extensions(pluginId: String): MutableList<T>

    fun extensionClassNames(pluginId: String): MutableSet<String>

    fun extensionFactory(): ExtensionFactory

    fun runtimeMode(): RuntimeMode

    fun development(): Boolean = RuntimeMode.DEVELOPMENT == runtimeMode()

    fun notDevelopment(): Boolean = !development()

    fun whichPlugin(clazz: Class<*>): PluginWrapper

    fun addPluginStateListener(listener: PluginStateListener)

    fun removePluginStateListener(listener: PluginStateListener)

    fun systemVersion(): String

    fun systemVersion(version: String)

    @Deprecated("")
    fun pluginsRoot(): Path

    fun pluginsRoots(): MutableList<Path>

    fun versionManager(): VersionManager
}