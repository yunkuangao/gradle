package com.yunkuangao

import com.yunkuangao.asm.ExtensionInfo
import com.yunkuangao.util.ClassUtils.Companion.getAllInterfacesNames
import mu.KotlinLogging

abstract class AbstractExtensionFinder(protected var pluginManager: PluginManager) : ExtensionFinder, PluginStateListener {

    private val logger = KotlinLogging.logger {}

    @Volatile
    protected var entries: MutableMap<String, MutableSet<String>> = mutableMapOf()
        get() {
            if (field.isEmpty()) {
                entries = readStorages()
            }
            return field
        }

    @Volatile
    protected lateinit var extensionInfos: MutableMap<String, ExtensionInfo>
    protected var checkForExtensionDependencies: Boolean = false

    abstract fun readPluginsStorages(): MutableMap<String, MutableSet<String>>

    abstract fun readClasspathStorages(): MutableMap<String, MutableSet<String>>

    override fun <T> find(type: Class<T>): MutableList<ExtensionWrapper<T>> {
        logger.debug { "Finding extensions of extension point '$type.name'" }
        val result: MutableList<ExtensionWrapper<T>> = mutableListOf()

        // add extensions found in classpath and plugins
        for (pluginId in entries.keys) {
            // classpath's extensions <=> pluginId = null
            val pluginExtensions: MutableList<ExtensionWrapper<T>> = find(type, pluginId)
            result.addAll(pluginExtensions)
        }
        if (result.isEmpty()) {
            logger.debug("No extensions found for extension point '{}'", type.name)
        } else {
            logger.debug("Found {} extensions for extension point '{}'", result.size, type.name)
        }

        // sort by "ordinal" property
        result.sortBy { it.getOrdinal() }
        return result
    }

    override fun <T> find(type: Class<T>, pluginId: String): MutableList<ExtensionWrapper<T>> {
        logger.debug("Finding extensions of extension point '{}' for plugin '{}'", type.name, pluginId)
        val result: MutableList<ExtensionWrapper<T>> = mutableListOf()

        // classpath's extensions <=> pluginId = null
        val classNames = findClassNames(pluginId)
        if (classNames.isEmpty()) {
            return result
        }
        val pluginWrapper = pluginManager.plugin(pluginId)
        if (PluginState.STARTED !== pluginWrapper.pluginState) {
            return result
        }
        logger.trace("Checking extensions from plugin '{}'", pluginId)
        val classLoader = pluginManager.pluginClassLoader(pluginId)
        for (className in classNames) {
            try {
                if (checkForExtensionDependencies) {
                    // Load extension annotation without initializing the class itself.
                    //
                    // If optional dependencies are used, the class loader might not be able
                    // to load the extension class because of missing optional dependencies.
                    //
                    // Therefore we're extracting the extension annotation via asm, in order
                    // to extract the required plugins for an extension. Only if all required
                    // plugins are currently available and started, the corresponding
                    // extension is loaded through the class loader.
                    val extensionInfo: ExtensionInfo = getExtensionInfo(className, classLoader)

                    // Make sure, that all plugins required by this extension are available.
                    val missingPluginIds: MutableList<String> = mutableListOf()
                    for (requiredPluginId in extensionInfo.plugins) {
                        val requiredPlugin = pluginManager.plugin(requiredPluginId)
                        if (!PluginState.STARTED.equals(requiredPlugin.pluginState)) {
                            missingPluginIds.add(requiredPluginId)
                        }
                    }
                    if (missingPluginIds.isNotEmpty()) {
                        val missing = StringBuilder()
                        for (missingPluginId in missingPluginIds) {
                            if (missing.isNotEmpty()) missing.append(", ")
                            missing.append(missingPluginId)
                        }
                        logger.trace("Extension '{}' is ignored due to missing plugins: {}", className, missing)
                        continue
                    }
                }
                logger.debug("Loading class '{}' using class loader '{}'", className, classLoader)
                val extensionClass = classLoader.loadClass(className) as Class<T>
                logger.debug("Checking extension type '{}'", className)
                if (type.isAssignableFrom(extensionClass)) {
                    val extensionWrapper: ExtensionWrapper<T> = createExtensionWrapper(extensionClass)
                    result.add(extensionWrapper)
                    logger.debug("Added extension '{}' with ordinal {}", className, extensionWrapper.getOrdinal())
                } else {
                    logger.trace("'{}' is not an extension for extension point '{}'", className, type.name)
                    if (RuntimeMode.DEVELOPMENT == pluginManager.runtimeMode()) {
                        checkDifferentClassLoaders(type, extensionClass)
                    }
                }
            } catch (e: ClassNotFoundException) {
                logger.error(e.message, e)
            }
        }
        if (result.isEmpty()) {
            logger.debug("No extensions found for extension point '{}'", type.name)
        } else {
            logger.debug("Found {} extensions for extension point '{}'", result.size, type.name)
        }

        // sort by "ordinal" property
        result.sortBy { it.getOrdinal() }
        return result
    }

    override fun <T> find(pluginId: String): MutableList<ExtensionWrapper<T>> {
        logger.debug("Finding extensions from plugin '{}'", pluginId)
        val result: MutableList<ExtensionWrapper<T>> = mutableListOf()
        val classNames = findClassNames(pluginId)
        if (classNames.isEmpty()) {
            return result
        }
        val pluginWrapper = pluginManager.plugin(pluginId)
        if (PluginState.STARTED !== pluginWrapper.pluginState) {
            return result
        }
        logger.trace("Checking extensions from plugin '{}'", pluginId)
        val classLoader = pluginManager.pluginClassLoader(pluginId)
        for (className in classNames) {
            try {
                logger.debug("Loading class '{}' using class loader '{}'", className, classLoader)
                val extensionClass = classLoader.loadClass(className) as Class<T>
                val extensionWrapper: ExtensionWrapper<T> = createExtensionWrapper(extensionClass)
                result.add(extensionWrapper)
                logger.debug("Added extension '{}' with ordinal {}", className, extensionWrapper.getOrdinal())
            } catch (e: ClassNotFoundException) {
                logger.error(e.message, e)
            } catch (e: NoClassDefFoundError) {
                logger.error(e.message, e)
            }
        }
        if (result.isEmpty()) {
            logger.debug("No extensions found for plugin '{}'", pluginId)
        } else {
            logger.debug("Found {} extensions for plugin '{}'", result.size, pluginId)
        }

        // sort by "ordinal" property
        result.sortBy { it.getOrdinal() }
        return result
    }

    override fun findClassNames(pluginId: String): MutableSet<String> {
        return entries[pluginId] ?: return mutableSetOf()
    }

    override fun pluginStateChanged(event: PluginStateEvent) {
        // TODO optimize (do only for some transitions)
        // clear cache
        entries = mutableMapOf()

        // By default we're assuming, that no checks for extension dependencies are necessary.
        //
        // A plugin, that has an optional dependency to other plugins, might lead to unloadable
        // Java classes (NoClassDefFoundError) at application runtime due to possibly missing
        // dependencies. Therefore we're enabling the check for optional extensions, if the
        // started plugin contains at least one optional plugin dependency.
        if (checkForExtensionDependencies == null && PluginState.STARTED == event.getPluginState()) {
            for (dependency in event.getPlugin().descriptor.getDependencies()) {
                if (dependency.optional) {
                    logger.debug("Enable check for extension dependencies via ASM.")
                    checkForExtensionDependencies = true
                    break
                }
            }
        }
    }

    protected open fun debugExtensions(extensions: MutableSet<String>) {
        if (logger.isDebugEnabled()) {
            if (extensions.isEmpty()) {
                logger.debug("No extensions found")
            } else {
                logger.debug("Found possible {} extensions:", extensions.size)
                for (extension in extensions) {
                    logger.debug("   $extension")
                }
            }
        }
    }

    private fun readStorages(): MutableMap<String, MutableSet<String>> {
        val result: MutableMap<String, MutableSet<String>> = LinkedHashMap()
        result.putAll(readClasspathStorages())
        result.putAll(readPluginsStorages())
        return result
    }

    /**
     * Returns the parameters of an [Extension] annotation without loading
     * the corresponding class into the class loader.
     *
     * @param className name of the class, that holds the requested [Extension] annotation
     * @param classLoader class loader to access the class
     * @return the contents of the [Extension] annotation or null, if the class does not
     * have an [Extension] annotation
     */
    private fun getExtensionInfo(className: String, classLoader: ClassLoader): ExtensionInfo {
        if (!extensionInfos.containsKey(className)) {
            logger.trace("Load annotation for '{}' using asm", className)
            val info: ExtensionInfo = ExtensionInfo.load(className, classLoader)
            extensionInfos[className] = info
        }
        return extensionInfos[className] ?: throw NotFoundException("the $className's extensionInfo not found")
    }

    private fun <T> createExtensionWrapper(extensionClass: Class<T>): ExtensionWrapper<T> {
        val extensionAnnotation = findExtensionAnnotation(extensionClass)
        val ordinal = extensionAnnotation.ordinal
        val descriptor = ExtensionDescriptor(ordinal, extensionClass)
        return ExtensionWrapper(descriptor, pluginManager.extensionFactory())
    }

    open fun findExtensionAnnotation(clazz: Class<*>): Extension {
        if (clazz.isAnnotationPresent(Extension::class.java)) {
            return clazz.getAnnotation(Extension::class.java)
        }

        // search recursively through all annotations
        for (annotation in clazz.annotations) {
            val annotationClass: Class<out Annotation> = annotation.annotationClass.java
            if (!annotationClass.name.startsWith("java.lang.annotation")) {
                return findExtensionAnnotation(annotationClass)
            }
        }
        throw ExtensionNotFoundException(clazz.name)
    }

    private fun checkDifferentClassLoaders(type: Class<*>, extensionClass: Class<*>) {
        val typeClassLoader = type.classLoader // class loader of extension point
        val extensionClassLoader = extensionClass.classLoader
        val match: Boolean = getAllInterfacesNames(extensionClass).contains(type.simpleName)
        if (match && extensionClassLoader != typeClassLoader) {
            // in this scenario the method 'isAssignableFrom' returns only FALSE
            // see http://www.coderanch.com/t/557846/java/java/FWIW-FYI-isAssignableFrom-isInstance-differing
            logger.error("Different class loaders: '{}' (E) and '{}' (EP)", extensionClassLoader, typeClassLoader)
        }
    }

}