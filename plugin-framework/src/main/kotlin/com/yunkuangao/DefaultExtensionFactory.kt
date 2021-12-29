package com.yunkuangao

import mu.KotlinLogging

open class DefaultExtensionFactory : ExtensionFactory {

    private val logger = KotlinLogging.logger {}

    override fun <T> create(extensionClass: Class<T>): T {
        logger.debug("Create instance for extension '{}'", extensionClass.name)
        return try {
            extensionClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw PluginRuntimeException(e)
        }
    }
}