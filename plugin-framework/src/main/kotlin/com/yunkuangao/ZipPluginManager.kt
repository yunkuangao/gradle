package com.yunkuangao

class ZipPluginManager : DefaultPluginManager() {

    override fun createPluginDescriptorFinder(): PluginDescriptorFinder {
        return PropertiesPluginDescriptorFinder()
    }

    override fun createPluginLoader(): PluginLoader {
        return CompoundPluginLoader()
            .add(DevelopmentPluginLoader(this), ::development)
            .add(DefaultPluginLoader(this), ::notDevelopment)
    }

    override fun createPluginRepository(): PluginRepository {
        return CompoundPluginRepository()
            .add(DevelopmentPluginRepository(pluginsRoots()), ::development)
            .add(DefaultPluginRepository(pluginsRoots()), ::notDevelopment)
    }

}