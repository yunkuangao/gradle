package com.yunkuangao

import mu.KotlinLogging
import java.io.Closeable
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer

abstract class AbstractPluginManager : PluginManager {

    private val logger = KotlinLogging.logger {}

    protected val pluginsRoots: MutableList<Path> = mutableListOf()
    protected open var extensionFinder: ExtensionFinder
    protected open var pluginDescriptorFinder: PluginDescriptorFinder
    protected open var plugins: MutableMap<String, PluginWrapper> = mutableMapOf()
    protected open var pluginClassLoaders: MutableMap<String, ClassLoader> = mutableMapOf()
    protected open var unresolvedPlugins: MutableList<PluginWrapper> = mutableListOf()
    protected open var resolvedPlugins: MutableList<PluginWrapper> = mutableListOf()
    protected open var startedPlugins: MutableList<PluginWrapper> = mutableListOf()
    protected open var pluginStateListeners: MutableList<PluginStateListener> = mutableListOf()
    protected open var runtimeMode: RuntimeMode = RuntimeMode.byNamed(System.getProperty(MODE_PROPERTY_NAME, RuntimeMode.DEPLOYMENT.toString()))
    protected open var systemVersion: String = "0.0.0"
    protected open var pluginRepository: PluginRepository
    protected open var pluginFactory: PluginFactory
    protected open var extensionFactory: ExtensionFactory
    protected open var pluginStatusProvider: PluginStatusProvider
    protected open var versionManager: VersionManager
    protected open var dependencyResolver: DependencyResolver
    protected open var pluginLoader: PluginLoader
    protected open var exactVersionAllowed = false

    constructor()

    constructor(vararg pluginsRoots: Path) : this(mutableListOf<Path>(*pluginsRoots))

    constructor(pluginsRoots: MutableList<Path>) {
        this.pluginsRoots.addAll(pluginsRoots)
    }

    init {
        if (pluginsRoots.isEmpty()) {
            pluginsRoots.addAll(createPluginsRoot())
        }
        extensionFinder = createExtensionFinder()
        pluginDescriptorFinder = createPluginDescriptorFinder()
        pluginRepository = createPluginRepository()
        pluginFactory = createPluginFactory()
        extensionFactory = createExtensionFactory()
        pluginStatusProvider = createPluginStatusProvider()
        versionManager = createVersionManager()
        dependencyResolver = DependencyResolver(versionManager)
        pluginLoader = createPluginLoader()
    }

    override fun systemVersion(version: String) {
        systemVersion = version
    }

    override fun systemVersion(): String {
        return systemVersion
    }

    override fun plugins(): MutableList<PluginWrapper> {
        return plugins.values.toMutableList()
    }

    override fun plugins(pluginState: PluginState): MutableList<PluginWrapper> {
        val plugins: MutableList<PluginWrapper> = mutableListOf()
        for (plugin in plugins()) {
            if (pluginState == plugin.pluginState) {
                plugins.add(plugin)
            }
        }
        return plugins
    }

    override fun resolvedPlugins(): MutableList<PluginWrapper> {
        return resolvedPlugins
    }

    override fun unresolvedPlugins(): MutableList<PluginWrapper> {
        return unresolvedPlugins
    }

    override fun startedPlugins(): MutableList<PluginWrapper> {
        return startedPlugins
    }

    override fun plugin(pluginId: String): PluginWrapper {
        return plugins[pluginId]!!
    }

    override fun extensionClassNames(pluginId: String): MutableSet<String> {
        return extensionFinder.findClassNames(pluginId)
    }

    override fun extensionFactory(): ExtensionFactory {
        return extensionFactory
    }

    open fun pluginLoader(): PluginLoader {
        return pluginLoader
    }

    override fun pluginsRoot(): Path {
        return pluginsRoots.stream().findFirst().orElseThrow { IllegalStateException("pluginsRoots have not been initialized, yet.") }
    }

    override fun pluginsRoots(): MutableList<Path> = pluginsRoots


    override fun loadPlugin(pluginPath: Path): String {
        require(!(Files.notExists(pluginPath))) { String.format("Specified plugin %s does not exist!", pluginPath) }
        logger.debug("Loading plugin from '{}'", pluginPath)
        val pluginWrapper = loadPluginFromPath(pluginPath)
        resolvePlugins()
        return pluginWrapper.descriptor.getPluginId()
    }

    override fun loadPlugins() {
        logger.debug("Lookup plugins in '{}'", pluginsRoots)
        // check for plugins roots
        if (pluginsRoots.isEmpty()) {
            logger.warn("No plugins roots configured")
            return
        }
        pluginsRoots.forEach(Consumer { path: Path ->
            if (Files.notExists(path) || !Files.isDirectory(path)) {
                logger.warn("No '{}' root", path)
            }
        })

        // get all plugin paths from repository
        val pluginPaths = pluginRepository.getPluginPaths()

        // check for no plugins
        if (pluginPaths.isEmpty()) {
            logger.info("No plugins")
            return
        }
        logger.debug("Found {} possible plugins: {}", pluginPaths.size, pluginPaths)

        // load plugins from plugin paths
        for (pluginPath in pluginPaths) {
            try {
                loadPluginFromPath(pluginPath)
            } catch (e: PluginRuntimeException) {
                logger.error(e.message, e)
            }
        }

        // resolve plugins
        try {
            resolvePlugins()
        } catch (e: PluginRuntimeException) {
            logger.error(e.message, e)
        }
    }

    override fun unloadPlugins() {
        // wrap resolvedPlugins in new list because of concurrent modification
        for (pluginWrapper in resolvedPlugins) {
            unloadPlugin(pluginWrapper.getPluginId())
        }
    }

    override fun unloadPlugin(pluginId: String): Boolean {
        return unloadPlugin(pluginId, true)
    }

    protected open fun unloadPlugin(pluginId: String, unloadDependents: Boolean): Boolean {
        try {
            if (unloadDependents) {
                val dependents: MutableList<String> = dependencyResolver.getDependents(pluginId)
                while (dependents.isNotEmpty()) {
                    val dependent = dependents.removeAt(0)
                    unloadPlugin(dependent, false)
                    dependents.addAll(0, dependencyResolver.getDependents(dependent))
                }
            }
            val pluginState = stopPlugin(pluginId, false)
            if (PluginState.STARTED === pluginState) {
                return false
            }
            val pluginWrapper = plugin(pluginId)
            logger.info("Unload plugin '{}'", getPluginLabel(pluginWrapper.descriptor))

            // remove the plugin
            plugins.remove(pluginId)
            resolvedPlugins().remove(pluginWrapper)
            firePluginStateEvent(PluginStateEvent(this, pluginWrapper, pluginState))

            // remove the classloader
            val pluginClassLoaders = pluginClassLoaders
            if (pluginClassLoaders.containsKey(pluginId)) {
                val classLoader = pluginClassLoaders.remove(pluginId)
                if (classLoader is Closeable) {
                    try {
                        (classLoader as Closeable).close()
                    } catch (e: IOException) {
                        throw PluginRuntimeException(e, "Cannot close classloader")
                    }
                }
            }
            return true
        } catch (e: IllegalArgumentException) {
            // ignore not found exceptions because this method is recursive
        }
        return false
    }

    override fun deletePlugin(pluginId: String): Boolean {
        checkPluginId(pluginId)
        val pluginWrapper = plugin(pluginId)
        // stop the plugin if it's started
        val pluginState = stopPlugin(pluginId)
        if (PluginState.STARTED === pluginState) {
            logger.error("Failed to stop plugin '{}' on delete", pluginId)
            return false
        }

        // get an instance of plugin before the plugin is unloaded
        // for reason see https://github.com/pf4j/pf4j/issues/309
        val plugin: Plugin = pluginWrapper.plugin ?: throw NotFoundException("plugin not found")
        if (!unloadPlugin(pluginId)) {
            logger.error("Failed to unload plugin '{}' on delete", pluginId)
            return false
        }

        // notify the plugin as it's deleted
        plugin.delete()
        val pluginPath: Path = pluginWrapper.pluginPath
        return pluginRepository.deletePluginPath(pluginPath)
    }

    /**
     * Start all active plugins.
     */
    override fun startPlugins() {
        for (pluginWrapper in resolvedPlugins) {
            val pluginState: PluginState = pluginWrapper.pluginState
            if (PluginState.DISABLED !== pluginState && PluginState.STARTED !== pluginState) {
                try {
                    logger.info("Start plugin '{}'", getPluginLabel(pluginWrapper.descriptor))
                    pluginWrapper.plugin?.start()
                    pluginWrapper.pluginState = PluginState.STARTED
                    pluginWrapper.failedException = null
                    startedPlugins.add(pluginWrapper)
                } catch (e: Exception) {
                    pluginWrapper.pluginState = PluginState.FAILED
                    pluginWrapper.failedException = e
                    logger.error("Unable to start plugin '{}'", getPluginLabel(pluginWrapper.descriptor), e)
                } catch (e: LinkageError) {
                    pluginWrapper.pluginState = PluginState.FAILED
                    pluginWrapper.failedException = e
                    logger.error("Unable to start plugin '{}'", getPluginLabel(pluginWrapper.descriptor), e)
                } finally {
                    firePluginStateEvent(PluginStateEvent(this, pluginWrapper, pluginState))
                }
            }
        }
    }

    /**
     * Start the specified plugin and its dependencies.
     */
    override fun startPlugin(pluginId: String): PluginState {
        checkPluginId(pluginId)
        val pluginWrapper = plugin(pluginId)
        val pluginDescriptor: PluginDescriptor = pluginWrapper.descriptor
        val pluginState: PluginState = pluginWrapper.pluginState
        if (PluginState.STARTED === pluginState) {
            logger.debug("Already started plugin '{}'", getPluginLabel(pluginDescriptor))
            return PluginState.STARTED
        }
        if (!resolvedPlugins.contains(pluginWrapper)) {
            logger.warn("Cannot start an unresolved plugin '{}'", getPluginLabel(pluginDescriptor))
            return pluginState
        }
        if (PluginState.DISABLED === pluginState) {
            // automatically enable plugin on manual plugin start
            if (!enablePlugin(pluginId)) {
                return pluginState
            }
        }
        for (dependency in pluginDescriptor.getDependencies()) {
            if (!dependency.optional || plugins.containsKey(dependency.pluginId)) {
                startPlugin(dependency.pluginId)
            }
        }
        logger.info("Start plugin '{}'", getPluginLabel(pluginDescriptor))
        pluginWrapper.plugin?.start()
        pluginWrapper.pluginState = PluginState.STARTED
        startedPlugins.add(pluginWrapper)
        firePluginStateEvent(PluginStateEvent(this, pluginWrapper, pluginState))
        return pluginWrapper.pluginState
    }

    /**
     * Stop all active plugins.
     */
    override fun stopPlugins() {
        // stop started plugins in reverse order
        startedPlugins.reverse()
        val itr = startedPlugins.iterator()
        while (itr.hasNext()) {
            val pluginWrapper = itr.next()
            val pluginState: PluginState = pluginWrapper.pluginState
            if (PluginState.STARTED === pluginState) {
                try {
                    logger.info("Stop plugin '{}'", getPluginLabel(pluginWrapper.descriptor))
                    pluginWrapper.plugin?.stop()
                    pluginWrapper.pluginState = PluginState.STOPPED
                    itr.remove()
                    firePluginStateEvent(PluginStateEvent(this, pluginWrapper, pluginState))
                } catch (e: PluginRuntimeException) {
                    logger.error(e.message, e)
                }
            }
        }
    }

    /**
     * Stop the specified plugin and it's dependents.
     */
    override fun stopPlugin(pluginId: String): PluginState {
        return stopPlugin(pluginId, true)
    }

    protected open fun stopPlugin(pluginId: String, stopDependents: Boolean): PluginState {
        checkPluginId(pluginId)
        val pluginWrapper = plugin(pluginId)
        val pluginDescriptor: PluginDescriptor = pluginWrapper.descriptor
        val pluginState: PluginState = pluginWrapper.pluginState
        if (PluginState.STOPPED === pluginState) {
            logger.debug("Already stopped plugin '{}'", getPluginLabel(pluginDescriptor))
            return PluginState.STOPPED
        }

        // test for disabled plugin
        if (PluginState.DISABLED === pluginState) {
            // do nothing
            return pluginState
        }
        if (stopDependents) {
            val dependents: MutableList<String> = dependencyResolver.getDependents(pluginId)
            while (dependents.isNotEmpty()) {
                val dependent = dependents.removeAt(0)
                stopPlugin(dependent, false)
                dependents.addAll(0, dependencyResolver.getDependents(dependent))
            }
        }
        logger.info("Stop plugin '{}'", getPluginLabel(pluginDescriptor))
        pluginWrapper.plugin?.stop()
        pluginWrapper.pluginState = PluginState.STOPPED
        startedPlugins.remove(pluginWrapper)
        firePluginStateEvent(PluginStateEvent(this, pluginWrapper, pluginState))
        return pluginWrapper.pluginState
    }

    protected open fun checkPluginId(pluginId: String) {
        require(plugins.containsKey(pluginId)) { String.format("Unknown pluginId %s", pluginId) }
    }

    override fun disablePlugin(pluginId: String): Boolean {
        checkPluginId(pluginId)
        val pluginWrapper = plugin(pluginId)
        val pluginDescriptor: PluginDescriptor = pluginWrapper.descriptor
        val pluginState: PluginState = pluginWrapper.pluginState
        if (PluginState.DISABLED === pluginState) {
            logger.debug("Already disabled plugin '{}'", getPluginLabel(pluginDescriptor))
            return true
        }
        if (PluginState.STOPPED === stopPlugin(pluginId)) {
            pluginWrapper.pluginState = PluginState.DISABLED
            firePluginStateEvent(PluginStateEvent(this, pluginWrapper, PluginState.STOPPED))
            pluginStatusProvider.disablePlugin(pluginId)
            logger.info("Disabled plugin '{}'", getPluginLabel(pluginDescriptor))
            return true
        }
        return false
    }

    override fun enablePlugin(pluginId: String): Boolean {
        checkPluginId(pluginId)
        val pluginWrapper = plugin(pluginId)
        if (!isPluginValid(pluginWrapper)) {
            logger.warn("Plugin '{}' can not be enabled", getPluginLabel(pluginWrapper.descriptor))
            return false
        }
        val pluginDescriptor: PluginDescriptor = pluginWrapper.descriptor
        val pluginState: PluginState = pluginWrapper.pluginState
        if (PluginState.DISABLED !== pluginState) {
            logger.debug("Plugin '{}' is not disabled", getPluginLabel(pluginDescriptor))
            return true
        }
        pluginStatusProvider.enablePlugin(pluginId)
        pluginWrapper.pluginState = PluginState.CREATED
        firePluginStateEvent(PluginStateEvent(this, pluginWrapper, pluginState))
        logger.info("Enabled plugin '{}'", getPluginLabel(pluginDescriptor))
        return true
    }

    override fun pluginClassLoader(pluginId: String): ClassLoader {
        return pluginClassLoaders[pluginId]!!
    }

    override fun <T> extensionClasses(pluginId: String): MutableList<Class<T>> {
        val extensionsWrapper: MutableList<ExtensionWrapper<T>> = extensionFinder.find(pluginId)
        val extensionClasses: MutableList<Class<T>> = mutableListOf()
        for (extensionWrapper in extensionsWrapper) {
            val c: Class<T> = extensionWrapper.descriptor.extensionClass
            extensionClasses.add(c)
        }
        return extensionClasses
    }

    override fun <T> extensionClasses(type: Class<T>): MutableList<Class<T>> {
        return getExtensionClasses(extensionFinder.find(type))
    }

    override fun <T> extensionClasses(type: Class<T>, pluginId: String): MutableList<Class<T>> {
        return getExtensionClasses(extensionFinder.find(type, pluginId))
    }

    open fun <T> getExtensions(type: Class<T>): MutableList<T> {
        return getExtensions(extensionFinder.find(type))
    }

    open fun <T> getExtensions(type: Class<T>, pluginId: String): MutableList<T> {
        return getExtensions(extensionFinder.find(type, pluginId))
    }

    open fun <T> getExtensions(pluginId: String): MutableList<T> {
        val extensionsWrapper: MutableList<ExtensionWrapper<T>> = extensionFinder.find(pluginId)
        val extensions: MutableList<T> = mutableListOf()
        for (extensionWrapper in extensionsWrapper) {
            try {
                extensionWrapper.extension?.let { extensions.add(it) }
            } catch (e: PluginRuntimeException) {
                logger.error("Cannot retrieve extension", e)
            }
        }
        return extensions
    }

    override fun whichPlugin(clazz: Class<*>): PluginWrapper {
        val classLoader = clazz.classLoader
        for (plugin in resolvedPlugins) {
            if (plugin.pluginClassLoader === classLoader) {
                return plugin
            }
        }
        throw PluginRuntimeException("no pluginClassLoader")
    }

    @Synchronized
    override fun addPluginStateListener(listener: PluginStateListener) {
        pluginStateListeners.add(listener)
    }

    @Synchronized
    override fun removePluginStateListener(listener: PluginStateListener) {
        pluginStateListeners.remove(listener)
    }

    open fun getVersion(): String {
        return Pf4jInfo.VERSION
    }

    protected abstract fun createPluginRepository(): PluginRepository

    protected abstract fun createPluginFactory(): PluginFactory

    protected abstract fun createExtensionFactory(): ExtensionFactory

    protected abstract fun createPluginDescriptorFinder(): PluginDescriptorFinder

    protected abstract fun createExtensionFinder(): ExtensionFinder

    protected abstract fun createPluginStatusProvider(): PluginStatusProvider

    protected abstract fun createPluginLoader(): PluginLoader

    protected abstract fun createVersionManager(): VersionManager

    protected open fun createPluginsRoot(): MutableList<Path> {
        var pluginsDir = System.getProperty(PLUGINS_DIR_PROPERTY_NAME)
        if (pluginsDir != null && pluginsDir.isNotEmpty()) {
            return pluginsDir.split(",").toTypedArray()
                .map { obj: String -> obj.trim { it <= ' ' } }
                .map { first: String -> Paths.get(first) }
                .toMutableList()
        }
        pluginsDir = if (development()) DEVELOPMENT_PLUGINS_DIR else DEFAULT_PLUGINS_DIR
        return mutableListOf(Paths.get(pluginsDir))
    }

    protected open fun isPluginValid(pluginWrapper: PluginWrapper): Boolean {
        var requires: String = pluginWrapper.descriptor.getRequires().trim()
        if (!exactVersionAllowed && requires.matches("^\\d+\\.\\d+\\.\\d+\$".toRegex())) {
            // If exact versions are not allowed in requires, rewrite to >= expression
            requires = ">=$requires"
        }
        if (systemVersion == "0.0.0" || versionManager.checkVersionConstraint(systemVersion, requires)) {
            return true
        }
        val pluginDescriptor: PluginDescriptor = pluginWrapper.descriptor
        logger.warn("Plugin '{}' requires a minimum system version of {}, and you have {}", getPluginLabel(pluginDescriptor), requires, systemVersion)
        return false
    }

    protected open fun isPluginDisabled(pluginId: String): Boolean {
        return pluginStatusProvider.isPluginDisabled(pluginId)
    }

    protected open fun resolvePlugins() {
        // retrieves the plugins descriptors
        val descriptors: MutableList<PluginDescriptor> = mutableListOf()
        for (plugin in plugins.values) {
            descriptors.add(plugin.descriptor)
        }
        val result: DependencyResolver.Companion.Result = dependencyResolver.resolve(descriptors)
        if (result.hasCyclicDependency()) {
            throw DependencyResolver.Companion.CyclicDependencyException()
        }
        val notFoundDependencies: MutableList<String> = result.getNotFoundDependencies()
        if (notFoundDependencies.isNotEmpty()) {
            throw DependencyResolver.Companion.DependenciesNotFoundException(notFoundDependencies)
        }
        val wrongVersionDependencies: MutableList<DependencyResolver.Companion.WrongDependencyVersion> = result.getWrongVersionDependencies()
        if (wrongVersionDependencies.isNotEmpty()) {
            throw DependencyResolver.Companion.DependenciesWrongVersionException(wrongVersionDependencies)
        }
        val sortedPlugins: MutableList<String> = result.sortedPlugins

        // move plugins from "unresolved" to "resolved"
        for (pluginId in sortedPlugins) {
            val pluginWrapper = plugins[pluginId]
            if (unresolvedPlugins.remove(pluginWrapper)) {
                val pluginState: PluginState = pluginWrapper!!.pluginState
                if (pluginState !== PluginState.DISABLED) {
                    pluginWrapper.pluginState = PluginState.RESOLVED
                }
                resolvedPlugins.add(pluginWrapper)
                logger.info("Plugin '{}' resolved", getPluginLabel(pluginWrapper.descriptor))
                firePluginStateEvent(PluginStateEvent(this, pluginWrapper, pluginState))
            }
        }
    }

    @Synchronized
    protected open fun firePluginStateEvent(event: PluginStateEvent) {
        for (listener in pluginStateListeners) {
            logger.trace("Fire '{}' to '{}'", event, listener)
            listener.pluginStateChanged(event)
        }
    }

    protected open fun loadPluginFromPath(pluginPath: Path): PluginWrapper {
        var pluginId = ""
        if (idForPath(pluginPath).isSuccess) throw PluginAlreadyLoadedException(pluginId, pluginPath)

        // Retrieve and validate the plugin descriptor
        val pluginDescriptorFinder = pluginDescriptorFinder
        logger.debug("Use '{}' to find plugins descriptors", pluginDescriptorFinder)
        logger.debug("Finding plugin descriptor for plugin '{}'", pluginPath)
        val pluginDescriptor = pluginDescriptorFinder.find(pluginPath)
        validatePluginDescriptor(pluginDescriptor)

        // Check there are no loaded plugins with the retrieved id
        pluginId = pluginDescriptor.getPluginId()
        if (plugins.containsKey(pluginId)) {
            val loadedPlugin = plugin(pluginId)
            throw PluginRuntimeException("""
    There is an already loaded plugin ({}) with the same id ({}) as the plugin at path '{}'. Simultaneous loading of plugins with the same PluginId is not currently supported.
    As a workaround you may include PluginVersion and PluginProvider in PluginId.
    """.trimIndent(), loadedPlugin, pluginId, pluginPath)
        }
        logger.debug("Found descriptor {}", pluginDescriptor)
        val pluginClassName = pluginDescriptor.getPluginClass()
        logger.debug("Class '{}' for plugin '{}'", pluginClassName, pluginPath)

        // load plugin
        logger.debug("Loading plugin '{}'", pluginPath)
        val pluginClassLoader = pluginLoader().loadPlugin(pluginPath, pluginDescriptor)
        logger.debug("Loaded plugin '{}' with class loader '{}'", pluginPath, pluginClassLoader)
        val pluginWrapper = createPluginWrapper(pluginDescriptor, pluginPath, pluginClassLoader)

        // test for disabled plugin
        if (isPluginDisabled(pluginDescriptor.getPluginId())) {
            logger.info("Plugin '{}' is disabled", pluginPath)
            pluginWrapper.pluginState = PluginState.DISABLED
        }

        // validate the plugin
        if (!isPluginValid(pluginWrapper)) {
            logger.warn("Plugin '{}' is invalid and it will be disabled", pluginPath)
            pluginWrapper.pluginState = PluginState.DISABLED
        }
        logger.debug("Created wrapper '{}' for plugin '{}'", pluginWrapper, pluginPath)
        pluginId = pluginDescriptor.getPluginId()

        // add plugin to the list with plugins
        plugins[pluginId] = pluginWrapper
        unresolvedPlugins.add(pluginWrapper)

        // add plugin class loader to the list with class loaders
        pluginClassLoaders[pluginId] = pluginClassLoader
        return pluginWrapper
    }

    /**
     * creates the plugin wrapper. override this if you want to prevent plugins having full access to the plugin manager
     *
     * @return
     */
    protected open fun createPluginWrapper(pluginDescriptor: PluginDescriptor, pluginPath: Path, pluginClassLoader: ClassLoader): PluginWrapper {
        // create the plugin wrapper
        logger.debug("Creating wrapper for plugin '{}'", pluginPath)
        val pluginWrapper = PluginWrapper(this, pluginDescriptor, pluginPath, pluginClassLoader)
        pluginWrapper.pluginFactory = pluginFactory
        return pluginWrapper
    }

    /**
     * Tests for already loaded plugins on given path.
     *
     * @param pluginPath the path to investigate
     * @return id of plugin or null if not loaded
     */
    protected open fun idForPath(pluginPath: Path): Result<String> {
        for (plugin in plugins.values) {
            if (plugin.pluginPath == pluginPath) {
                return Result.success(plugin.getPluginId())
            }
        }
        return Result.failure(NotFoundException("plugin of pluginId not found"))
    }

    /**
     * Override this to change the validation criteria.
     *
     * @param descriptor the plugin descriptor to validate
     * @throws PluginRuntimeException if validation fails
     */
    protected open fun validatePluginDescriptor(descriptor: PluginDescriptor) {
        if (descriptor.getPluginId().isEmpty()) {
            throw PluginRuntimeException("Field 'id' cannot be empty")
        }

        if (descriptor.getVersion().isEmpty()) {
            throw PluginRuntimeException("Field 'version' cannot be empty")
        }
    }

    protected open fun getPluginLabel(pluginDescriptor: PluginDescriptor): String {
        return pluginDescriptor.getPluginId() + "@" + pluginDescriptor.getVersion()
    }

    protected open fun <T> getExtensionClasses(extensionsWrapper: MutableList<ExtensionWrapper<T>>): MutableList<Class<T>> {
        val extensionClasses: MutableList<Class<T>> = mutableListOf()
        for (extensionWrapper in extensionsWrapper) {
            val c = extensionWrapper.descriptor.extensionClass
            extensionClasses.add(c)
        }
        return extensionClasses
    }

    protected open fun <T> getExtensions(extensionsWrapper: MutableList<ExtensionWrapper<T>>): MutableList<T> {
        val extensions: MutableList<T> = mutableListOf()
        for (extensionWrapper in extensionsWrapper) {
            try {
                extensions.add(extensionWrapper.extension)
            } catch (e: PluginRuntimeException) {
                logger.error("Cannot retrieve extension", e)
            }
        }
        return extensions
    }


    companion object {
        const val PLUGINS_DIR_PROPERTY_NAME = "pf.pluginsDir"
        const val MODE_PROPERTY_NAME = "pf.mode"
        const val DEFAULT_PLUGINS_DIR = "plugins"
        const val DEVELOPMENT_PLUGINS_DIR = "../plugins"
    }
}