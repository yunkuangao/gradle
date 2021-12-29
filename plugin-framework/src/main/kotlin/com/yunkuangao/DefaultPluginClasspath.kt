package com.yunkuangao

class DefaultPluginClasspath : PluginClasspath() {

    init {
        addClassesDirectories(CLASSES_DIR)
        addJarsDirectories(LIB_DIR)
    }

    companion object {
        const val CLASSES_DIR = "classes"
        const val LIB_DIR = "lib"
    }
}