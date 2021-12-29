package com.yunkuangao

import java.nio.file.Path

class PluginWrapper(
    pluginManager: PluginManager,
    descriptor: PluginDescriptor,
    pluginPath: Path,
    pluginClassLoader: ClassLoader,
) {
    var pluginManager: PluginManager = pluginManager
        private set
    var descriptor: PluginDescriptor
        private set
    var pluginPath: Path
        private set
    var pluginClassLoader: ClassLoader
        private set
    lateinit var pluginFactory: PluginFactory
    var pluginState: PluginState
    var runtimeMode: RuntimeMode
        private set
    var failedException: Throwable? = null
    var plugin: Plugin? = null
        get() = field ?: pluginFactory.create(this)

    init {
        this.descriptor = descriptor
        this.pluginPath = pluginPath
        this.pluginClassLoader = pluginClassLoader
        pluginState = PluginState.CREATED
        runtimeMode = pluginManager.runtimeMode()
    }

    fun getPluginId(): String {
        return descriptor.getPluginId()
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + descriptor.getPluginId().hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val other = other as PluginWrapper
        return descriptor.getPluginId() == other.descriptor.getPluginId()
    }

    override fun toString(): String {
        return "PluginWrapper [descriptor=$descriptor, pluginPath=$pluginPath]"
    }

}