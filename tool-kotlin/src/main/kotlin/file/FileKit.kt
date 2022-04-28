package file

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun writeFile(
    path: String,
    data: ByteArray,
) {
    existDirectory(path.substring(0, path.lastIndexOf(sep)))
    val file = File(path)
    file.writeBytes(data)
}

fun createDirectory(path: String) {
    Files.createDirectory(Paths.get(path))
}

fun existDirectory(path: String) {
    if (!File(path).exists()) {
        createDirectory(path)
    }
}

fun validFileName(name: String): String = name.map {
    if ("~!@#$%^&*，。；:‘’\\{【】[]}|/".contains(it)) ' '
    else it
}.joinToString("")