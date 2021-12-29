package com.yunkuangao

interface PluginDescriptor {
    fun getPluginId(): String

    fun getPluginDescription(): String

    fun getPluginClass(): String

    fun getVersion(): String

    fun getRequires(): String

    fun getProvider(): String

    fun getLicense(): String

    fun getDependencies(): MutableList<PluginDependency>
}