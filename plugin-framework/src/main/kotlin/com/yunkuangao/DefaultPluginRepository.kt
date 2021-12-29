package com.yunkuangao

import com.yunkuangao.util.*
import com.yunkuangao.util.FileUtils.Companion.findWithEnding
import com.yunkuangao.util.FileUtils.Companion.optimisticDelete
import mu.KotlinLogging
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.nio.file.Path

class DefaultPluginRepository : BasePluginRepository {

    private val logger = KotlinLogging.logger {}

    constructor(vararg pluginsRoots: Path) : this(mutableListOf<Path>(*pluginsRoots))

    constructor(pluginsRoots: MutableList<Path>) : super(pluginsRoots) {
        val pluginsFilter = AndFileFilter(DirectoryFileFilter())
        pluginsFilter.addFileFilter(NotFileFilter(createHiddenPluginFilter()))
        filter = pluginsFilter
    }

    override fun getPluginPaths(): MutableList<Path> {
        extractZipFiles()
        return super.getPluginPaths()
    }

    override fun deletePluginPath(pluginPath: Path): Boolean {
        optimisticDelete(findWithEnding(pluginPath, ".zip", ".ZIP", ".Zip"))
        return super.deletePluginPath(pluginPath)
    }

    protected fun createHiddenPluginFilter(): FileFilter {
        return OrFileFilter(HiddenFilter())
    }

    private fun extractZipFiles() {
        pluginsRoots.stream()
            .flatMap { path -> streamFiles(path, ZipFileFilter()) }
            .map { obj: File -> obj.toPath() }
            .forEach { filePath: Path -> expandIfZip(filePath) }
    }

    private fun expandIfZip(filePath: Path) {
        try {
            FileUtils.expandIfZip(filePath)
        } catch (e: IOException) {
            logger.error("Cannot expand plugin zip '{}'", filePath)
            logger.error(e.message, e)
        }
    }

}