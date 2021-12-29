package com.yunkuangao

import java.nio.file.Path

open class SecurePluginManagerWrapper(
    private val currentPluginId: String,
    private val original: PluginManager,
) : PluginManager {

    protected var pluginStateListeners: MutableList<PluginStateListener> = mutableListOf()

    private val listenerWrapper: PluginStateListenerWrapper = PluginStateListenerWrapper()

    override fun development(): Boolean {
        return original.development()
    }

    override fun notDevelopment(): Boolean {
        return original.notDevelopment()
    }

    override fun plugins(): MutableList<PluginWrapper> {
        return mutableListOf(plugin(currentPluginId))
    }

    override fun plugins(pluginState: PluginState): MutableList<PluginWrapper> = plugins().filter { p: PluginWrapper -> p.pluginState === pluginState }.toMutableList()

    override fun resolvedPlugins(): MutableList<PluginWrapper> = plugins().filter { p: PluginWrapper -> p.pluginState.ordinal >= PluginState.RESOLVED.ordinal }.toMutableList()

    override fun unresolvedPlugins(): MutableList<PluginWrapper> = mutableListOf()

    override fun startedPlugins(): MutableList<PluginWrapper> = plugins(PluginState.STARTED)

    override fun plugin(pluginId: String): PluginWrapper {
        return if (currentPluginId == pluginId) {
            original.plugin(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getPlugin for foreign pluginId!")
        }
    }

    override fun loadPlugins() = throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute loadPlugins!")


    override fun loadPlugin(pluginPath: Path): String = throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute loadPlugin!")


    override fun startPlugins() = throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute startPlugins!")


    override fun startPlugin(pluginId: String): PluginState = throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute startPlugin!")


    override fun stopPlugins() {
        throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute stopPlugins!")
    }

    override fun stopPlugin(pluginId: String): PluginState {
        return if (currentPluginId == pluginId) {
            original.stopPlugin(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute stopPlugin for foreign pluginId!")
        }
    }

    override fun unloadPlugins() = throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute unloadPlugins!")


    override fun unloadPlugin(pluginId: String): Boolean =
        if (currentPluginId == pluginId) {
            original.unloadPlugin(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute unloadPlugin for foreign pluginId!")
        }


    override fun disablePlugin(pluginId: String): Boolean =
        if (currentPluginId == pluginId) {
            original.disablePlugin(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute disablePlugin for foreign pluginId!")
        }


    override fun enablePlugin(pluginId: String): Boolean = throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute enablePlugin!")

    override fun deletePlugin(pluginId: String): Boolean =
        if (currentPluginId == pluginId) {
            original.deletePlugin(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute deletePlugin for foreign pluginId!")
        }

    override fun pluginClassLoader(pluginId: String): ClassLoader =
        if (currentPluginId == pluginId) {
            original.pluginClassLoader(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getPluginClassLoader for foreign pluginId!")
        }

    override fun <T> extensionClasses(pluginId: String): MutableList<Class<T>> =
        if (currentPluginId == pluginId) {
            original.extensionClasses(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getExtensionClasses for foreign pluginId!")
        }

    override fun <T> extensionClasses(type: Class<T>): MutableList<Class<T>> = extensionClasses(type, currentPluginId)

    override fun <T> extensionClasses(type: Class<T>, pluginId: String): MutableList<Class<T>> =
        if (currentPluginId == pluginId) {
            original.extensionClasses(type, pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getExtensionClasses for foreign pluginId!")
        }

    override fun <T> extensions(type: Class<T>): MutableList<T> = extensions(type, currentPluginId)

    override fun <T> extensions(type: Class<T>, pluginId: String): MutableList<T> =
        if (currentPluginId == pluginId) {
            original.extensions(type, pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getExtensions for foreign pluginId!")
        }

    override fun <T> extensions(pluginId: String): MutableList<T> =
        if (currentPluginId == pluginId) {
            original.extensions(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getExtensions for foreign pluginId!")
        }

    override fun extensionClassNames(pluginId: String): MutableSet<String> =
        if (currentPluginId == pluginId) {
            original.extensionClassNames(pluginId)
        } else {
            throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getExtensionClassNames for foreign pluginId!")
        }

    override fun extensionFactory(): ExtensionFactory = original.extensionFactory()

    override fun runtimeMode(): RuntimeMode = original.runtimeMode()

    override fun whichPlugin(clazz: Class<*>): PluginWrapper {
        val classLoader = clazz.classLoader
        val plugin = plugin(currentPluginId)
        return if (plugin.pluginClassLoader === classLoader) {
            plugin
        } else throw PluginRuntimeException("the ${clazz.javaClass.name}'s pluginClassLoader not found")
    }

    override fun addPluginStateListener(listener: PluginStateListener) {
        if (pluginStateListeners.isEmpty()) {
            original.addPluginStateListener(listenerWrapper)
        }
        pluginStateListeners.add(listener)
    }

    override fun removePluginStateListener(listener: PluginStateListener) {
        pluginStateListeners.remove(listener)
        if (pluginStateListeners.isEmpty()) {
            original.removePluginStateListener(listenerWrapper)
        }
    }

    override fun systemVersion(version: String) {
        throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute setSystemVersion!")
    }

    override fun systemVersion(): String = original.systemVersion()

    override fun pluginsRoot(): Path {
        throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getPluginsRoot!")
    }

    override fun pluginsRoots(): MutableList<Path> {
        throw IllegalAccessError(PLUGIN_PREFIX + currentPluginId + " tried to execute getPluginsRoots!")
    }

    override fun versionManager(): VersionManager = original.versionManager()

    private inner class PluginStateListenerWrapper : PluginStateListener {
        override fun pluginStateChanged(event: PluginStateEvent) {
            if (event.getPlugin().getPluginId() == currentPluginId) {
                for (listener in pluginStateListeners) {
                    listener.pluginStateChanged(event)
                }
            }
        }
    }

    companion object {
        private const val PLUGIN_PREFIX = "Plugin "
    }
}