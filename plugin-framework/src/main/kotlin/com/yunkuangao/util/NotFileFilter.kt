package com.yunkuangao.util

import java.io.File
import java.io.FileFilter

class NotFileFilter(private val filter: FileFilter) : FileFilter {

    override fun accept(file: File): Boolean = !filter.accept(file)

}