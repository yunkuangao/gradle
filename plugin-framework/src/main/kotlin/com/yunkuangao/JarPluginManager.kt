package com.yunkuangao

import java.nio.file.Path

open class JarPluginManager: DefaultPluginManager {

    constructor() : super()

    constructor(vararg pluginsRoots: Path) :super(*pluginsRoots)

    override fun createPluginDescriptorFinder(): PluginDescriptorFinder {
        return ManifestPluginDescriptorFinder()
    }

    override fun createPluginLoader(): PluginLoader {
        return CompoundPluginLoader()
            .add(DevelopmentPluginLoader(this), ::development)
            .add(JarPluginLoader(this), ::notDevelopment)
    }

    override fun createPluginRepository(): PluginRepository {
        return CompoundPluginRepository()
            .add(DevelopmentPluginRepository(pluginsRoots), ::development)
            .add(JarPluginRepository(pluginsRoots), ::notDevelopment)
    }

}