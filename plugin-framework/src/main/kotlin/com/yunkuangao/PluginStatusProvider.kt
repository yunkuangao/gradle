package com.yunkuangao

interface PluginStatusProvider {

    fun isPluginDisabled(pluginId: String): Boolean

    fun disablePlugin(pluginId: String)

    fun enablePlugin(pluginId: String)
}