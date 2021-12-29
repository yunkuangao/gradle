package com.yunkuangao.plugindemo.app

import com.yunkuangao.ExtensionPoint

/**
 * Greeting is the extension point for plugins.
 */
interface Greeting : ExtensionPoint {
    // a value with default implementation
    val greeting: String
        get() = "Hello World!"

    fun greetPerson(person: String = "John Doe"): String
}
