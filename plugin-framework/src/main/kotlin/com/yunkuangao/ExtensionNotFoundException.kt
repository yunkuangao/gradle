package com.yunkuangao

open class ExtensionNotFoundException(
    private val extensionName: String,
) : ExtensionRuntimeException("Extension '{}' not found", extensionName) {

    fun extensionName(): String {
        return extensionName
    }
}