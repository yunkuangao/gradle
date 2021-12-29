package com.yunkuangao

import java.util.*

class DefaultPluginDescriptor(
    private var pluginId: String = "",
    private var pluginDescription: String = "",
    private var pluginClass: String = Plugin::class.java.name,
    private var version: String = "",
    private var requires: String = "*",
    private var provider: String = "",
    private var license: String = "",
) : PluginDescriptor {

    private var dependencies: MutableList<PluginDependency> = mutableListOf()

    init {
        dependencies = mutableListOf()
    }

    fun addDependency(dependency: PluginDependency) {
        dependencies.add(dependency)
    }

    override fun toString(): String {
        return ("PluginDescriptor [pluginId=" + pluginId + ", pluginClass="
                + pluginClass + ", version=" + version + ", provider="
                + provider + ", dependencies=" + dependencies + ", description="
                + pluginDescription + ", requires=" + requires + ", license="
                + license + "]")
    }

    fun setPluginId(pluginId: String): DefaultPluginDescriptor {
        this.pluginId = pluginId
        return this
    }

    fun setPluginDescription(pluginDescription: String): PluginDescriptor {
        this.pluginDescription = pluginDescription
        return this
    }

    fun setPluginClass(pluginClassName: String): PluginDescriptor {
        pluginClass = pluginClassName
        return this
    }

    fun setPluginVersion(version: String): DefaultPluginDescriptor {
        this.version = version
        return this
    }

    fun setProvider(provider: String): PluginDescriptor {
        this.provider = provider
        return this
    }

    fun setRequires(requires: String): PluginDescriptor {
        this.requires = requires
        return this
    }

    fun setDependencies(dependencies: String): PluginDescriptor {
        if (dependencies.trim().isNotEmpty()) {
            val tokens = dependencies.split(",").toTypedArray()
            for (dependency in tokens) {
                val tmp = dependency.trim()
                if (tmp.isNotEmpty()) {
                    this.dependencies.add(PluginDependency(tmp))
                }
            }
        }
        return this
    }

    fun setLicense(license: String): PluginDescriptor {
        this.license = license
        return this
    }

    override fun getPluginId(): String {
        return pluginId
    }

    override fun getPluginDescription(): String {
        return pluginDescription
    }

    override fun getPluginClass(): String {
        return pluginClass
    }

    override fun getVersion(): String {
        return version
    }

    override fun getRequires(): String {
        return requires
    }

    override fun getProvider(): String {
        return provider
    }

    override fun getLicense(): String {
        return license
    }

    override fun getDependencies(): MutableList<PluginDependency> {
        return dependencies
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is DefaultPluginDescriptor) return false
        return (pluginId == other.pluginId) &&
                (pluginDescription == other.pluginDescription) &&
                (pluginClass == other.pluginClass) &&
                (version == other.version) &&
                (requires == other.requires) &&
                (provider == other.provider) && (dependencies == other.dependencies) &&
                (license == other.license)
    }

    override fun hashCode(): Int {
        return Objects.hash(pluginId, pluginDescription, pluginClass, version, requires, provider, dependencies, license)
    }

}