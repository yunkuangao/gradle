package com.yunkuangao

import java.util.*

class PluginDependency(dependency: String) {
    var pluginId: String
        private set
    var pluginVersionSupport: String = "*"
        private set
    var optional: Boolean
        private set

    init {
        val index = dependency.indexOf('@')
        if (index == -1) {
            pluginId = dependency
        } else {
            pluginId = dependency.substring(0, index)
            if (dependency.length > index + 1) {
                pluginVersionSupport = dependency.substring(index + 1)
            }
        }

        // 如果插件 ID 以问号结尾，则依赖项被视为可选。
        this.optional = pluginId.endsWith("?")
        if (this.optional) {
            pluginId = pluginId.substring(0, pluginId.length - 1)
        }
    }

    override fun toString(): String {
        return ("PluginDependency [pluginId=" + pluginId + ", pluginVersionSupport="
                + pluginVersionSupport + ", optional="
                + optional + "]")
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is PluginDependency) return false
        return (optional == o.optional)
                && (pluginId == o.pluginId)
                && (pluginVersionSupport == o.pluginVersionSupport)
    }

    override fun hashCode(): Int {
        return Objects.hash(pluginId, pluginVersionSupport, optional)
    }

}