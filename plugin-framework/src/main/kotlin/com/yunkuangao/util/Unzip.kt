package com.yunkuangao.util

import com.yunkuangao.util.FileUtils.Companion.delete
import mu.KotlinLogging
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class Unzip {

    private val logger = KotlinLogging.logger {}

    private lateinit var destination: File

    private lateinit var source: File

    constructor()

    constructor(destination: File, source: File) {
        this.source = source
        this.destination = destination
    }

    fun setSource(source: File) {
        this.source = source
    }

    fun setDestination(destination: File) {
        this.destination = destination
    }

    fun extract() {
        logger.debug { "Extract content of '$source' to '$destination'" }

        // delete destination directory if exists
        if (destination.exists() && destination.isDirectory) {
            delete(destination.toPath())
        }
        ZipInputStream(FileInputStream(source)).use { zipInputStream ->
            while (true) {
                val zipEntry: ZipEntry = zipInputStream.nextEntry ?: break
                val file = File(destination, zipEntry.name)

                // create intermediary directories - sometimes zip don't add them
                val dir = File(file.parent)
                mkdirsOrThrow(dir)
                if (zipEntry.isDirectory) {
                    mkdirsOrThrow(file)
                } else {
                    val buffer = ByteArray(1024)
                    var length: Int
                    FileOutputStream(file).use { fos ->
                        while (zipInputStream.read(buffer).also { length = it } >= 0) {
                            fos.write(buffer, 0, length)
                        }
                    }
                }
            }
        }
    }

    private fun mkdirsOrThrow(dir: File) {
        if (!dir.exists() && !dir.mkdirs()) {
            throw IOException("Failed to create directory $dir")
        }
    }

}