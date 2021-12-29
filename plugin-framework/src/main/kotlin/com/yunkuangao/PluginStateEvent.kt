package com.yunkuangao

import java.util.*

class PluginStateEvent(
    private val source: PluginManager,
    private val plugin: PluginWrapper,
    private val oldState: PluginState,
) : EventObject(source) {

    override fun getSource(): PluginManager {
        return super.getSource() as PluginManager
    }

    fun getPlugin(): PluginWrapper {
        return plugin
    }

    fun getPluginState(): PluginState {
        return plugin.pluginState
    }

    fun getOldState(): PluginState {
        return oldState
    }

    override fun toString(): String {
        return "PluginStateEvent [plugin=" + plugin.getPluginId() +
                ", newState=" + getPluginState().toString() +
                ", oldState=" + oldState +
                ']'
    }
}