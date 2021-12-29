package com.yunkuangao.util

import java.io.File
import java.io.FileFilter
import java.util.*

class AndFileFilter : FileFilter {

    var fileFilters: MutableList<FileFilter>

    constructor() : this(mutableListOf())

    constructor(vararg fileFilters: FileFilter) : this(fileFilters.toMutableList())

    constructor(fileFilters: MutableList<FileFilter>) {
        this.fileFilters = fileFilters
    }

    fun addFileFilter(fileFilter: FileFilter): AndFileFilter {
        fileFilters.add(fileFilter)
        return this
    }

    fun removeFileFilter(fileFilter: FileFilter): Boolean {
        return fileFilters.remove(fileFilter)
    }

    override fun accept(file: File): Boolean {
        if (fileFilters.isEmpty()) {
            return false
        }
        for (fileFilter in fileFilters) {
            if (!fileFilter.accept(file)) {
                return false
            }
        }
        return true
    }
}