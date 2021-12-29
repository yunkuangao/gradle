package com.yunkuangao

import java.util.*

interface PluginStateListener : EventListener {
    fun pluginStateChanged(event: PluginStateEvent)
}