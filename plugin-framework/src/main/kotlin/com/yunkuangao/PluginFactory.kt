package com.yunkuangao

interface PluginFactory {
    fun create(pluginWrapper: PluginWrapper): Plugin?
}