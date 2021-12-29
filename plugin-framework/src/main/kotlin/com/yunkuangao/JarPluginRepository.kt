package com.yunkuangao

import com.yunkuangao.util.JarFileFilter
import java.nio.file.Path

open class JarPluginRepository : BasePluginRepository {

    constructor(vararg pluginsRoots: Path) : this(mutableListOf<Path>(*pluginsRoots))

    constructor(pluginsRoots: MutableList<Path>) : super(pluginsRoots, JarFileFilter())

}