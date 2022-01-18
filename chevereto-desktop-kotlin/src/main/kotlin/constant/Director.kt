package constant

import java.io.File

const val cheveretoPath = "chevereto"

/**
 * 返回用户图片目录
 */
fun musicDirector(): String {
    val home = File(System.getProperty("user.home"))
    return home.listFiles()?.filter {
        it.name == "picture" || it.name == "图片"
    }?.get(0)?.absolutePath ?: (System.getProperty("user.dir") + File.separator + "picture")
}

