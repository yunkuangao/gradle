package com.yunkuangao.util

import java.io.File
import java.io.FileFilter
import java.util.*

open class OrFileFilter : FileFilter {

    open var fileFilters: MutableList<FileFilter>

    constructor() : this(mutableListOf())

    constructor(vararg fileFilters: FileFilter) : this(fileFilters.toMutableList())

    constructor(fileFilters: MutableList<FileFilter>) {
        this.fileFilters = fileFilters
    }

    fun addFileFilter(fileFilter: FileFilter): OrFileFilter {
        this.fileFilters.add(fileFilter)
        return this
    }

    fun removeFileFilter(fileFilter: FileFilter): Boolean {
        return this.fileFilters.remove(fileFilter)
    }

    override fun accept(file: File): Boolean {
        if (this.fileFilters.isEmpty()) {
            return true
        }
        for (fileFilter in this.fileFilters) {
            if (fileFilter.accept(file)) {
                return true
            }
        }
        return false
    }
}