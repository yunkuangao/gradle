package com.yunkuangao

open class DefaultExtensionFinder(
    protected var pluginManager: PluginManager,
) : ExtensionFinder, PluginStateListener {

    protected var finders: MutableList<ExtensionFinder> = mutableListOf()

    init {
        add(LegacyExtensionFinder(pluginManager))
        //add(ServiceProviderExtensionFinder(pluginManager))
    }

    override fun <T> find(type: Class<T>): MutableList<ExtensionWrapper<T>> {
        val extensions: MutableList<ExtensionWrapper<T>> = mutableListOf()
        for (finder in finders) {
            extensions.addAll(finder.find(type))
        }
        return extensions
    }

    override fun <T> find(type: Class<T>, pluginId: String): MutableList<ExtensionWrapper<T>> {
        val extensions: MutableList<ExtensionWrapper<T>> = mutableListOf()
        for (finder in finders) {
            extensions.addAll(finder.find(type, pluginId))
        }
        return extensions
    }

    override fun <T> find(pluginId: String): MutableList<ExtensionWrapper<T>> {
        val extensions: MutableList<ExtensionWrapper<T>> = mutableListOf()
        for (finder in finders) {
            extensions.addAll(finder.find(pluginId))
        }
        return extensions
    }

    override fun findClassNames(pluginId: String): MutableSet<String> {
        val classNames: MutableSet<String> = HashSet()
        for (finder in finders) {
            classNames.addAll(finder.findClassNames(pluginId))
        }
        return classNames
    }

    override fun pluginStateChanged(event: PluginStateEvent) {
        for (finder in finders) {
            if (finder is PluginStateListener) {
                (finder as PluginStateListener).pluginStateChanged(event)
            }
        }
    }

    fun addServiceProviderExtensionFinder(): DefaultExtensionFinder {
        return add(ServiceProviderExtensionFinder(pluginManager))
    }

    fun add(finder: ExtensionFinder): DefaultExtensionFinder {
        finders.add(finder)
        return this
    }

}