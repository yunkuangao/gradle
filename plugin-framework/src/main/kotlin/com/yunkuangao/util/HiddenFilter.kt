package com.yunkuangao.util

import java.io.File
import java.io.FileFilter

class HiddenFilter : FileFilter {

    override fun accept(file: File): Boolean = file.isHidden

}