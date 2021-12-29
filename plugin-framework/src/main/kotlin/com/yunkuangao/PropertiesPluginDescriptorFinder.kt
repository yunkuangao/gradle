package com.yunkuangao

import com.yunkuangao.util.FileUtils.Companion.closePath
import com.yunkuangao.util.FileUtils.Companion.getPath
import com.yunkuangao.util.FileUtils.Companion.isZipOrJarFile
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class PropertiesPluginDescriptorFinder(private val propertiesFileName: String) : PluginDescriptorFinder {

    private val logger = KotlinLogging.logger {}

    constructor() : this(DEFAULT_PROPERTIES_FILE_NAME)

    override fun isApplicable(pluginPath: Path): Boolean {
        return Files.exists(pluginPath) && (Files.isDirectory(pluginPath) || isZipOrJarFile(pluginPath))
    }

    override fun find(pluginPath: Path): PluginDescriptor {
        val properties = readProperties(pluginPath)
        return createPluginDescriptor(properties)
    }

    protected fun readProperties(pluginPath: Path): Properties {
        val propertiesPath = getPropertiesPath(pluginPath, propertiesFileName)
        val properties = Properties()
        try {
            logger.debug("Lookup plugin descriptor in '{}'", propertiesPath)
            if (Files.notExists(propertiesPath)) {
                throw PluginRuntimeException("Cannot find '{}' path", propertiesPath)
            }
            try {
                Files.newInputStream(propertiesPath).use { input -> properties.load(input) }
            } catch (e: IOException) {
                throw PluginRuntimeException(e)
            }
        } finally {
            closePath(propertiesPath)
        }
        return properties
    }

    protected fun getPropertiesPath(pluginPath: Path, propertiesFileName: String): Path {
        return if (Files.isDirectory(pluginPath)) {
            pluginPath.resolve(Paths.get(propertiesFileName))
        } else try {
            getPath(pluginPath, propertiesFileName)!!
        } catch (e: IOException) {
            throw PluginRuntimeException(e)
        }

        // it's a zip or jar file
    }

    protected fun createPluginDescriptor(properties: Properties): PluginDescriptor {
        val pluginDescriptor = createPluginDescriptorInstance()

        // TODO validate !!!
        val id = properties.getProperty(PLUGIN_ID)
        pluginDescriptor.setPluginId(id)
        val description = properties.getProperty(PLUGIN_DESCRIPTION)
        if (description.isEmpty()) {
            pluginDescriptor.setPluginDescription("")
        } else {
            pluginDescriptor.setPluginDescription(description)
        }
        val clazz = properties.getProperty(PLUGIN_CLASS)
        if (clazz.isNotEmpty()) {
            pluginDescriptor.setPluginClass(clazz)
        }
        val version = properties.getProperty(PLUGIN_VERSION)
        if (version.isNotEmpty()) {
            pluginDescriptor.setPluginVersion(version)
        }
        val provider = properties.getProperty(PLUGIN_PROVIDER)
        pluginDescriptor.setProvider(provider)
        val dependencies = properties.getProperty(PLUGIN_DEPENDENCIES)
        pluginDescriptor.setDependencies(dependencies)
        val requires = properties.getProperty(PLUGIN_REQUIRES)
        if (requires.isNotEmpty()) {
            pluginDescriptor.setRequires(requires)
        }
        pluginDescriptor.setLicense(properties.getProperty(PLUGIN_LICENSE))
        return pluginDescriptor
    }

    protected fun createPluginDescriptorInstance(): DefaultPluginDescriptor {
        return DefaultPluginDescriptor()
    }


    companion object {
        const val DEFAULT_PROPERTIES_FILE_NAME = "plugin.properties"

        const val PLUGIN_ID = "plugin.id"
        const val PLUGIN_DESCRIPTION = "plugin.description"
        const val PLUGIN_CLASS = "plugin.class"
        const val PLUGIN_VERSION = "plugin.version"
        const val PLUGIN_PROVIDER = "plugin.provider"
        const val PLUGIN_DEPENDENCIES = "plugin.dependencies"
        const val PLUGIN_REQUIRES = "plugin.requires"
        const val PLUGIN_LICENSE = "plugin.license"

    }
}