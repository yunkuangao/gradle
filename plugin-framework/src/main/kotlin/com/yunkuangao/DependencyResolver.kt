package com.yunkuangao

import com.yunkuangao.util.DirectedGraph
import mu.KotlinLogging

open class DependencyResolver(versionManager: VersionManager) {

    private val logger = KotlinLogging.logger {}

    private lateinit var versionManager: VersionManager

    private var dependenciesGraph: DirectedGraph<String> = DirectedGraph()
    private var dependentsGraph: DirectedGraph<String> = DirectedGraph()
    private var resolved = false

    fun resolve(plugins: MutableList<PluginDescriptor>): Result {
        val pluginByIds: MutableMap<String, PluginDescriptor> = HashMap()
        for (plugin in plugins) {
            addPlugin(plugin)
            pluginByIds[plugin.getPluginId()] = plugin
        }
        logger.debug("Graph: {}", dependenciesGraph)

        val sortedPlugins: MutableList<String> = dependenciesGraph.reverseTopologicalSort()
        logger.debug("Plugins order: {}", sortedPlugins)

        val result: Result = Result(sortedPlugins)
        resolved = true
        for (pluginId in sortedPlugins) {
            if (!pluginByIds.containsKey(pluginId)) {
                result.addNotFoundDependency(pluginId)
            }
        }

        // check dependencies versions
        for (plugin in plugins) {
            val pluginId = plugin.getPluginId()
            val existingVersion = plugin.getVersion()
            val dependents = getDependents(pluginId).toMutableList()
            while (dependents.isNotEmpty()) {
                val dependentId: String = dependents.removeAt(0)
                val dependent = pluginByIds[dependentId] ?: throw NotFoundException("${plugin.getPluginId()} not found")
                val requiredVersion = getDependencyVersionSupport(dependent, pluginId)
                val ok = checkDependencyVersion(requiredVersion, existingVersion)
                if (!ok) {
                    result.addWrongDependencyVersion(WrongDependencyVersion(pluginId, dependentId, existingVersion, requiredVersion))
                }
            }
        }
        return result
    }

    /**
     * Retrieves the plugins ids that the given plugin id directly depends on.
     *
     * @param pluginId the unique plugin identifier, specified in its metadata
     * @return an immutable list of dependencies (new list for each call)
     */
    fun getDependencies(pluginId: String): MutableList<String> {
        checkResolved()
        return dependenciesGraph.getNeighbors(pluginId)
    }

    /**
     * Retrieves the plugins ids that the given content is a direct dependency of.
     *
     * @param pluginId the unique plugin identifier, specified in its metadata
     * @return an immutable list of dependents (new list for each call)
     */
    fun getDependents(pluginId: String): MutableList<String> {
        checkResolved()
        return dependentsGraph.getNeighbors(pluginId)
    }

    /**
     * Check if an existing version of dependency is compatible with the required version (from plugin descriptor).
     *
     * @param requiredVersion
     * @param existingVersion
     * @return
     */
    protected fun checkDependencyVersion(requiredVersion: String, existingVersion: String): Boolean {
        return versionManager.checkVersionConstraint(existingVersion, requiredVersion)
    }

    private fun addPlugin(descriptor: PluginDescriptor) {
        val pluginId = descriptor.getPluginId()
        val dependencies = descriptor.getDependencies()
        if (dependencies.isEmpty()) {
            dependenciesGraph.addVertex(pluginId)
            dependentsGraph.addVertex(pluginId)
        } else {
            var edgeAdded = false
            for (dependency in dependencies) {
                // Don't register optional plugins in the dependency graph to avoid automatic disabling of the plugin,
                // if an optional dependency is missing.
                if (!dependency.optional) {
                    edgeAdded = true
                    dependenciesGraph.addEdge(pluginId, dependency.pluginId)
                    dependentsGraph.addEdge(dependency.pluginId, pluginId)
                }
            }

            // Register the plugin without dependencies, if all of its dependencies are optional.
            if (!edgeAdded) {
                dependenciesGraph.addVertex(pluginId)
                dependentsGraph.addVertex(pluginId)
            }
        }
    }

    private fun checkResolved() {
        check(resolved) { "Call 'resolve' method first" }
    }

    private fun getDependencyVersionSupport(dependent: PluginDescriptor, dependencyId: String): String {
        val dependencies = dependent.getDependencies()
        for (dependency in dependencies) {
            if (dependencyId == dependency.pluginId) {
                return dependency.pluginVersionSupport
            }
        }
        throw IllegalStateException("Cannot find a dependency with id '" + dependencyId +
                "' for plugin '" + dependent.getPluginId() + "'")
    }


    companion object {

        open class Result internal constructor(sortedPlugins: MutableList<String>) {
            private var cyclicDependency = false
            private val notFoundDependencies
                    : MutableList<String>

            var sortedPlugins: MutableList<String>
            private val wrongVersionDependencies: MutableList<WrongDependencyVersion>

            init {
                if (sortedPlugins == null) {
                    cyclicDependency = true
                    this.sortedPlugins = mutableListOf()
                } else {
                    this.sortedPlugins = sortedPlugins
                }
                notFoundDependencies = mutableListOf()
                wrongVersionDependencies = mutableListOf()
            }

            fun hasCyclicDependency(): Boolean {
                return cyclicDependency
            }

            fun getNotFoundDependencies(): MutableList<String> {
                return notFoundDependencies
            }

            fun getWrongVersionDependencies(): MutableList<WrongDependencyVersion> {
                return wrongVersionDependencies
            }

            fun addNotFoundDependency(pluginId: String) {
                notFoundDependencies.add(pluginId)
            }

            fun addWrongDependencyVersion(wrongDependencyVersion: WrongDependencyVersion) {
                wrongVersionDependencies.add(wrongDependencyVersion)
            }
        }

        open class WrongDependencyVersion internal constructor(
            var dependencyId: String,
            var dependentId: String,
            var existingVersion: String,
            var requiredVersion: String,
        ) {
            override fun toString(): String {
                return "WrongDependencyVersion{" +
                        "dependencyId='" + dependencyId + '\'' +
                        ", dependentId='" + dependentId + '\'' +
                        ", existingVersion='" + existingVersion + '\'' +
                        ", requiredVersion='" + requiredVersion + '\'' +
                        '}'
            }
        }

        class CyclicDependencyException : PluginRuntimeException("Cyclic dependencies")

        class DependenciesNotFoundException(dependencies: MutableList<String>) : PluginRuntimeException("Dependencies '{}' not found", dependencies) {
            val dependencies: MutableList<String>

            init {
                this.dependencies = dependencies
            }
        }

        class DependenciesWrongVersionException(dependencies: MutableList<WrongDependencyVersion>) :
            PluginRuntimeException("Dependencies '{}' have wrong version", dependencies) {
            val dependencies: MutableList<WrongDependencyVersion>

            init {
                this.dependencies = dependencies
            }
        }

    }
}