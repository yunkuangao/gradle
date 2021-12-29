package com.yunkuangao

import java.util.*

open class PluginClasspath {

    private val classesDirectories: MutableSet<String> = mutableSetOf()
    private val jarsDirectories: MutableSet<String> = mutableSetOf()

    fun getClassesDirectories(): Set<String> {
        return classesDirectories
    }

    fun addClassesDirectories(vararg classesDirectories: String): PluginClasspath {
        return addClassesDirectories(classesDirectories.toList())
    }

    fun addClassesDirectories(classesDirectories: Collection<String>): PluginClasspath {
        this.classesDirectories.addAll(classesDirectories)
        return this
    }

    fun getJarsDirectories(): Set<String> {
        return jarsDirectories
    }

    fun addJarsDirectories(vararg jarsDirectories: String): PluginClasspath {
        return addJarsDirectories(jarsDirectories.toList())
    }

    fun addJarsDirectories(jarsDirectories: Collection<String>): PluginClasspath {
        this.jarsDirectories.addAll(jarsDirectories)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PluginClasspath) return false
        return (classesDirectories == other.classesDirectories)
                && (jarsDirectories == other.jarsDirectories)
    }

    override fun hashCode(): Int {
        return Objects.hash(classesDirectories, jarsDirectories)
    }
}