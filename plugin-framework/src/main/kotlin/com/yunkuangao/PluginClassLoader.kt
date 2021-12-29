package com.yunkuangao

import com.yunkuangao.ClassLoadingStrategy.Source.*
import mu.KotlinLogging
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.util.*

open class PluginClassLoader(
    private var pluginManager: PluginManager,
    private var pluginDescriptor: PluginDescriptor,
    parent: ClassLoader,
    private var classLoadingStrategy: ClassLoadingStrategy,
) : URLClassLoader(emptyArray<URL>(), parent) {

    private val logger = KotlinLogging.logger {}

    constructor(pluginManager: PluginManager, pluginDescriptor: PluginDescriptor, parent: ClassLoader) : this(pluginManager, pluginDescriptor, parent, ClassLoadingStrategy.PDA)

    override fun addURL(url: URL) {
        logger.debug { "Add '$url'" }
        super.addURL(url)
    }

    fun addFile(file: File) {
        try {
            addURL(file.canonicalFile.toURI().toURL())
        } catch (e: IOException) {
//            throw new RuntimeException(e);
            logger.error(e.message, e)
        }
    }

    override fun loadClass(className: String): Class<*> {
        synchronized(getClassLoadingLock(className)) {

            // first check whether it's a system class, delegate to the system loader
            if (className.startsWith(JAVA_PACKAGE_PREFIX)) {
                return findSystemClass(className)
            }

            // if the class is part of the plugin engine use parent class loader
            if (className.startsWith(PLUGIN_PACKAGE_PREFIX) && !className.startsWith("org.pf4j.demo") && !className.startsWith("org.pf4j.test")) {
//                log.trace("Delegate the loading of PF4J class '{}' to parent", className);
                return parent.loadClass(className)
            }
            logger.trace { "Received request to load class '$className'" }

            // second check whether it's already been loaded
            val loadedClass = findLoadedClass(className)
            if (loadedClass != null) {
                logger.trace { "Found loaded class '$className'" }
                return loadedClass
            }

            for (classLoadingSource in classLoadingStrategy.getSources()) {
                var c: Class<*>? = null
                try {
                    c = when (classLoadingSource) {
                        APPLICATION -> super.loadClass(className)
                        PLUGIN -> findClass(className)
                        DEPENDENCIES -> loadClassFromDependencies(className)
                    }
                } catch (ignored: ClassNotFoundException) {
                }
                if (c != null) {
                    logger.trace("Found class '{}' in {} classpath", className, classLoadingSource)
                    return c
                } else {
                    logger.trace("Couldn't find class '{}' in {} classpath", className, classLoadingSource)
                }
            }

            throw ClassNotFoundException(className)
        }
    }

    override fun getResource(name: String): URL? {
        logger.trace { "Received request to load resource '$name'" }

        for (classLoadingSource in classLoadingStrategy.getSources()) {
            val url: URL? = when (classLoadingSource) {
                APPLICATION -> super.getResource(name)
                PLUGIN -> findResource(name)
                DEPENDENCIES -> findResourceFromDependencies(name)
            }
            if (url != null) {
                logger.trace("Found resource '{}' in {} classpath", name, classLoadingSource)
                return url
            } else {
                logger.trace("Couldn't find resource '{}' in {}", name, classLoadingSource)
            }
        }

        return null
    }

    override fun getResources(name: String): Enumeration<URL> {
        val resources: MutableList<URL> = mutableListOf()
        logger.trace { "Received request to load resources '$name'" }
        for (classLoadingSource in classLoadingStrategy.getSources()) {
            when (classLoadingSource) {
                APPLICATION -> if (parent != null) {
                    resources.addAll(Collections.list(parent.getResources(name)))
                }
                PLUGIN -> resources.addAll(Collections.list(findResources(name)))
                DEPENDENCIES -> resources.addAll(findResourcesFromDependencies(name))
            }
        }
        return Collections.enumeration(resources)
    }

    protected fun loadClassFromDependencies(className: String): Class<*>? {
        logger.trace { "Search in dependencies for class '$className'" }
        val dependencies = pluginDescriptor.getDependencies()
        for (dependency in dependencies) {
            val classLoader = pluginManager.pluginClassLoader(dependency.pluginId)

            // If the dependency is marked as optional, its class loader might not be available.
            if (dependency.optional) {
                continue
            }
            try {
                return classLoader.loadClass(className)
            } catch (e: ClassNotFoundException) {
                // try next dependency
            }
        }
        return null
    }

    protected fun findResourceFromDependencies(name: String): URL? {
        logger.trace { "Search in dependencies for resource '$name'" }
        val dependencies = pluginDescriptor.getDependencies()
        for (dependency in dependencies) {
            val classLoader = pluginManager.pluginClassLoader(dependency.pluginId) as PluginClassLoader

            // If the dependency is marked as optional, its class loader might not be available.
            if (dependency.optional) {
                continue
            }
            val url = classLoader.findResource(name)
            if (Objects.nonNull(url)) {
                return url
            }
        }
        return null
    }

    protected fun findResourcesFromDependencies(name: String): Collection<URL> {
        logger.trace { "Search in dependencies for resources '$name'" }
        val results: MutableList<URL> = mutableListOf()
        val dependencies = pluginDescriptor.getDependencies()
        for (dependency in dependencies) {
            val classLoader = pluginManager.pluginClassLoader(dependency.pluginId) as PluginClassLoader

            // If the dependency is marked as optional, its class loader might not be available.
            results.addAll(Collections.list(classLoader.findResources(name)))
        }
        return results
    }

    companion object {

        private const val JAVA_PACKAGE_PREFIX = "java."
        private const val PLUGIN_PACKAGE_PREFIX = "org.pf4j."
    }

}