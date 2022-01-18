package util

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun writeFile(
    path: String,
    data: ByteArray,
) {
    createDirection(path.substring(0, path.lastIndexOf(File.separator)))
    val file = File(path)
    file.writeBytes(data)
}

fun createDirection(path: String) {
    Files.createDirectories(Paths.get(path))
}