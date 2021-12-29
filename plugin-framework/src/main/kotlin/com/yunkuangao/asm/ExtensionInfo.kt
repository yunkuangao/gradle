package com.yunkuangao.asm

import mu.KotlinLogging
import org.objectweb.asm.ClassReader
import java.io.IOException
import java.util.*

class ExtensionInfo(val className: String) {

    private val logger = KotlinLogging.logger {}

    var plugins: MutableList<String> = mutableListOf()
    var points: MutableList<String> = mutableListOf()
    var ordinal = 0

    companion object {

        private val logger = KotlinLogging.logger {}

        fun load(className: String, classLoader: ClassLoader): ExtensionInfo {
            try {
                classLoader.getResourceAsStream(className.replace('.', '/') + ".class").use { input ->
                    val info = ExtensionInfo(className)
                    ClassReader(input).accept(ExtensionVisitor(info), ClassReader.SKIP_DEBUG)
                    return info
                }
            } catch (e: IOException) {
                logger.error(e.message, e)
                throw e
            }
        }
    }
}