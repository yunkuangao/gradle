package com.yunkuangao.util

import java.io.File
import java.io.FileFilter

class NameFileFilter(private var name: String) : FileFilter {

    override fun accept(file: File): Boolean = file.name.equals(name, ignoreCase = true)

}