package com.yunkuangao

import com.yunkuangao.util.FileUtils.Companion.expandIfZip
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths

open class DefaultPluginManager : AbstractPluginManager {

    private val logger = KotlinLogging.logger {}

    constructor() : super()

    constructor(vararg pluginsRoots: Path) : super(*pluginsRoots)

    constructor(pluginsRoots: MutableList<Path>) : super(pluginsRoots)

    init {
        if (development()) {
            addPluginStateListener(LoggingPluginStateListener())
        }
        logger.info("PF4J version {} in '{}' mode", getVersion(), runtimeMode)
    }

    override fun createPluginDescriptorFinder(): PluginDescriptorFinder {
        return CompoundPluginDescriptorFinder()
            .add(PropertiesPluginDescriptorFinder())
            .add(ManifestPluginDescriptorFinder())
    }

    override fun createExtensionFinder(): ExtensionFinder {
        val extensionFinder = DefaultExtensionFinder(this)
        addPluginStateListener(extensionFinder)
        return extensionFinder
    }

    override fun createPluginFactory(): PluginFactory {
        return DefaultPluginFactory()
    }

    override fun createExtensionFactory(): ExtensionFactory {
        return DefaultExtensionFactory()
    }

    override fun createPluginStatusProvider(): PluginStatusProvider {
        val configDir = System.getProperty(PLUGINS_DIR_CONFIG_PROPERTY_NAME)
        val configPath = if (configDir != null) Paths.get(configDir) else pluginsRoots.stream()
            .findFirst()
            .orElseThrow { IllegalArgumentException("No pluginsRoot configured") }
        return DefaultPluginStatusProvider(configPath)
    }

    override fun createPluginRepository(): PluginRepository {
        return CompoundPluginRepository()
            .add(DevelopmentPluginRepository(pluginsRoots()), ::development)
            .add(JarPluginRepository(pluginsRoots()), ::notDevelopment)
            .add(DefaultPluginRepository(pluginsRoots()), ::notDevelopment)
    }

    override fun createPluginLoader(): PluginLoader {
        return CompoundPluginLoader()
            .add(DevelopmentPluginLoader(this), ::development)
            .add(JarPluginLoader(this), ::notDevelopment)
            .add(DefaultPluginLoader(this), ::notDevelopment)
    }

    override fun createVersionManager(): VersionManager {
        return DefaultVersionManager()
    }

    override fun loadPluginFromPath(pluginPath: Path): PluginWrapper {
        return try {
            super.loadPluginFromPath(expandIfZip(pluginPath))
        } catch (e: Exception) {
            logger.warn("Failed to unzip $pluginPath", e)
            throw e
        }
    }

    override fun <T> extensions(type: Class<T>): MutableList<T> = getExtensions(extensionFinder.find(type));

    override fun <T> extensions(type: Class<T>, pluginId: String): MutableList<T> = getExtensions(extensionFinder.find(type, pluginId))

    override fun <T> extensions(pluginId: String): MutableList<T> {
        val extensionsWrapper: MutableList<ExtensionWrapper<T>> = extensionFinder.find(pluginId)
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

    override fun runtimeMode(): RuntimeMode = runtimeMode

    override fun versionManager(): VersionManager = versionManager

    companion object {
        const val PLUGINS_DIR_CONFIG_PROPERTY_NAME = "pf4j.pluginsConfigDir"
    }
}