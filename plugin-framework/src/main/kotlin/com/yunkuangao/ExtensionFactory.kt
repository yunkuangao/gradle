package com.yunkuangao

interface ExtensionFactory {
    fun <T> create(extensionClass: Class<T>): T
}