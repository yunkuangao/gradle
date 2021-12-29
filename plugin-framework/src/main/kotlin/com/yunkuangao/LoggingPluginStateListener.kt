package com.yunkuangao

import mu.KotlinLogging

class LoggingPluginStateListener : PluginStateListener {

    private val logger = KotlinLogging.logger {}

    override fun pluginStateChanged(event: PluginStateEvent) {
        logger.debug("The state of plugin '{}' has changed from '{}' to '{}'", event.getPlugin().getPluginId(),
            event.getOldState(), event.getPluginState())
    }
}