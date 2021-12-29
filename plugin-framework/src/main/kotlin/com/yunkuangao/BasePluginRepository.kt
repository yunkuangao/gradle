package com.yunkuangao

import com.yunkuangao.util.FileUtils.Companion.delete
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

open class BasePluginRepository : PluginRepository {

    protected val pluginsRoots: MutableList<Path>
    protected var filter: FileFilter = FileFilter { true }
    protected var comparator: Comparator<File>

    constructor(vararg pluginsRoots: Path) : this(pluginsRoots.toMutableList())

    constructor(pluginsRoots: MutableList<Path>) : this(pluginsRoots, FileFilter { true })

    constructor(pluginsRoots: MutableList<Path>, filter: FileFilter) {
        this.pluginsRoots = pluginsRoots
        this.filter = filter

        this.comparator = Comparator.comparingLong { obj: File -> obj.lastModified() }
    }

    override fun getPluginPaths(): MutableList<Path> {
        return pluginsRoots.stream().flatMap { path: Path -> streamFiles(path, filter) }.sorted(comparator).map { obj: File -> obj.toPath() }.collect(Collectors.toList())
    }

    override fun deletePluginPath(pluginPath: Path): Boolean {
        return if (!filter.accept(pluginPath.toFile())) {
            false
        } else try {
            delete(pluginPath)
            true
        } catch (e: NoSuchFileException) {
            false // Return false on not found to be compatible with previous API (#135)
        } catch (e: IOException) {
            throw PluginRuntimeException(e)
        }
    }

    protected fun streamFiles(directory: Path, filter: FileFilter): Stream<File> {
        val files = directory.toFile().listFiles(filter)
        return if (files != null) Arrays.stream(files) else Stream.empty()
    }

}