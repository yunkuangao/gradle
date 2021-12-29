package com.yunkuangao.util

import java.io.File
import java.io.FileFilter
import java.util.*

open class ExtensionFileFilter(val extension: String) : FileFilter {

    override fun accept(file: File): Boolean {
        return file.name.uppercase(Locale.getDefault()).endsWith(extension.uppercase(Locale.getDefault()))
    }

}