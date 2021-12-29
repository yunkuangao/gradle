package com.yunkuangao

import com.yunkuangao.util.FileUtils.Companion.findFile
import com.yunkuangao.util.FileUtils.Companion.isJarFile
import com.yunkuangao.util.FileUtils.Companion.isZipFile
import com.yunkuangao.util.FileUtils.Companion.isZipOrJarFile
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile
import java.util.jar.Manifest
import java.util.zip.ZipFile

open class ManifestPluginDescriptorFinder : PluginDescriptorFinder {

    private val logger = KotlinLogging.logger {}

    override fun isApplicable(pluginPath: Path): Boolean {
        return Files.exists(pluginPath) && (Files.isDirectory(pluginPath) || isZipOrJarFile(pluginPath))
    }

    override fun find(pluginPath: Path): PluginDescriptor {
        val manifest = readManifest(pluginPath)
        return createPluginDescriptor(manifest)
    }

    protected open fun readManifest(pluginPath: Path): Manifest {
        if (isJarFile(pluginPath)) {
            return readManifestFromJar(pluginPath)
        }
        return if (isZipFile(pluginPath)) {
            readManifestFromZip(pluginPath)
        } else readManifestFromDirectory(pluginPath)
    }

    protected open fun createPluginDescriptor(manifest: Manifest): PluginDescriptor {
        val pluginDescriptor = createPluginDescriptorInstance()

        // TODO validate !!!
        val attributes = manifest.mainAttributes
        pluginDescriptor.setPluginId(attributes.getValue(PLUGIN_ID) ?: throw NotFoundException("pluginId not found"))
        pluginDescriptor.setPluginClass(attributes.getValue(PLUGIN_CLASS) ?: throw NotFoundException("pluginClass not found"))
        pluginDescriptor.setPluginVersion(attributes.getValue(PLUGIN_VERSION) ?: throw NotFoundException("pluginVersion not found"))
        pluginDescriptor.setPluginDescription(attributes.getValue(PLUGIN_DESCRIPTION) ?: "")
        pluginDescriptor.setProvider(attributes.getValue(PLUGIN_PROVIDER) ?: "")
        pluginDescriptor.setDependencies(attributes.getValue(PLUGIN_DEPENDENCIES) ?: "")
        pluginDescriptor.setRequires(attributes.getValue(PLUGIN_REQUIRES) ?: "")
        pluginDescriptor.setLicense(attributes.getValue(PLUGIN_LICENSE)?:"")

        return pluginDescriptor
    }

    protected open fun createPluginDescriptorInstance(): DefaultPluginDescriptor {
        return DefaultPluginDescriptor()
    }

    protected open fun readManifestFromJar(jarPath: Path): Manifest {
        try {
            JarFile(jarPath.toFile()).use { jar -> return jar.manifest }
        } catch (e: IOException) {
            throw PluginRuntimeException(e, "Cannot read manifest from {}", jarPath)
        }
    }

    protected open fun readManifestFromZip(zipPath: Path): Manifest {
        try {
            ZipFile(zipPath.toFile()).use { zip ->
                val manifestEntry = zip.getEntry("classes/META-INF/MANIFEST.MF")
                zip.getInputStream(manifestEntry).use { manifestInput -> return Manifest(manifestInput) }
            }
        } catch (e: IOException) {
            throw PluginRuntimeException(e, "Cannot read manifest from {}", zipPath)
        }
    }

    protected open fun readManifestFromDirectory(pluginPath: Path): Manifest {
        // legacy (the path is something like "classes/META-INF/MANIFEST.MF")
        val manifestPath: Path = findFile(pluginPath, "MANIFEST.MF")
        logger.debug("Lookup plugin descriptor in '{}'", manifestPath)
        if (Files.notExists(manifestPath)) {
            throw PluginRuntimeException("Cannot find '{}' path", manifestPath)
        }
        try {
            Files.newInputStream(manifestPath).use { input -> return Manifest(input) }
        } catch (e: IOException) {
            throw PluginRuntimeException(e, "Cannot read manifest from {}", pluginPath)
        }
    }

    companion object {
        const val PLUGIN_ID = "Plugin-Id"
        const val PLUGIN_DESCRIPTION = "Plugin-Description"
        const val PLUGIN_CLASS = "Plugin-Class"
        const val PLUGIN_VERSION = "Plugin-Version"
        const val PLUGIN_PROVIDER = "Plugin-Provider"
        const val PLUGIN_DEPENDENCIES = "Plugin-Dependencies"
        const val PLUGIN_REQUIRES = "Plugin-Requires"
        const val PLUGIN_LICENSE = "Plugin-License"
    }
}