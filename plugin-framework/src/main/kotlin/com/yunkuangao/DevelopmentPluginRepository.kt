package com.yunkuangao

import com.yunkuangao.util.*
import java.io.FileFilter
import java.nio.file.Path

class DevelopmentPluginRepository(pluginsRoots: MutableList<Path>) : BasePluginRepository(pluginsRoots) {

    constructor(vararg pluginsRoots: Path) : this(mutableListOf<Path>(*pluginsRoots))

    init {
        val pluginsFilter = AndFileFilter(DirectoryFileFilter())
        pluginsFilter.addFileFilter(NotFileFilter(createHiddenPluginFilter()))
        filter = pluginsFilter
    }

    protected fun createHiddenPluginFilter(): FileFilter {
        val hiddenPluginFilter = OrFileFilter(HiddenFilter())

        hiddenPluginFilter
            .addFileFilter(NameFileFilter(MAVEN_BUILD_DIR))
            .addFileFilter(NameFileFilter(GRADLE_BUILD_DIR))
        return hiddenPluginFilter
    }


    companion object {
        const val MAVEN_BUILD_DIR = "target"
        const val GRADLE_BUILD_DIR = "build"
    }
}