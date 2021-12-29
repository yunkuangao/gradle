package com.yunkuangao

import mu.KotlinLogging

open class Plugin(protected open var wrapper: PluginWrapper) {

    private val logger = KotlinLogging.logger {}

    open fun start() {}

    open fun stop() {}

    open fun delete() {}

}