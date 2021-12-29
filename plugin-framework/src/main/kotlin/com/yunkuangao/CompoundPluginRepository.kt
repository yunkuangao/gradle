package com.yunkuangao

import java.nio.file.Path
import java.util.function.BooleanSupplier

class CompoundPluginRepository: PluginRepository {

    private val repositories: MutableList<PluginRepository> = mutableListOf()

    fun add(repository: PluginRepository): CompoundPluginRepository {
        repositories.add(repository)
        return this
    }

    fun add(repository: PluginRepository, condition: BooleanSupplier): CompoundPluginRepository {
        return if (condition.asBoolean) {
            add(repository)
        } else this
    }

    override fun getPluginPaths(): MutableList<Path> {
        val paths: MutableSet<Path> = LinkedHashSet()
        for (repository in repositories) {
            paths.addAll(repository.getPluginPaths())
        }
        return paths.toMutableList()
    }

    override fun deletePluginPath(pluginPath: Path): Boolean {
        for (repository in repositories) {
            if (repository.deletePluginPath(pluginPath)) {
                return true
            }
        }
        return false
    }

}