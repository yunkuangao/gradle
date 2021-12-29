package com.yunkuangao.util

import java.io.File
import java.io.FileFilter

class DirectoryFileFilter : FileFilter {

    override fun accept(file: File): Boolean = file.isDirectory

}