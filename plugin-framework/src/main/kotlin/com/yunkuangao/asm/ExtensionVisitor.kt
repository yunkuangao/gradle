package com.yunkuangao.asm

import com.yunkuangao.Extension
import mu.KotlinLogging
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class ExtensionVisitor(val extensionInfo: ExtensionInfo) : ClassVisitor(ASM_VERSION) {

    private val logger = KotlinLogging.logger {}

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        return if (Type.getType(descriptor).className != Extension::class.java.name) {
            super.visitAnnotation(descriptor, visible)
        } else object : AnnotationVisitor(ASM_VERSION) {
            override fun visitArray(name: String): AnnotationVisitor {
                return if ("ordinal" == name || "plugins" == name || "points" == name) {
                    object : AnnotationVisitor(ASM_VERSION, super.visitArray(name)) {
                        override fun visit(key: String, value: Any) {
                            logger.debug { "Load annotation attribute $name = $value (${value.javaClass.name})" }
                            if ("ordinal" == name) {
                                extensionInfo.ordinal = value.toString().toInt()
                            } else if ("plugins" == name) {
                                if (value is String) {
                                    logger.debug { "Found plugin $value" }
                                    extensionInfo.plugins.add(value)
                                } else if (value is Array<*> && value.isArrayOf<String>()) {
                                    logger.debug { "Found plugins $value" }
                                    extensionInfo.plugins.addAll(value as MutableList<String>)
                                } else {
                                    logger.debug { "Found plugin $value" }
                                    extensionInfo.plugins.add(value.toString())
                                }
                            } else {
                                val pointClassName = (value as Type).className
                                logger.debug { "Found point $pointClassName" }
                                extensionInfo.points.add(pointClassName)
                            }
                            super.visit(key, value)
                        }
                    }
                } else super.visitArray(name)
            }
        }
    }

    companion object {
        const val ASM_VERSION = Opcodes.ASM7
    }
}