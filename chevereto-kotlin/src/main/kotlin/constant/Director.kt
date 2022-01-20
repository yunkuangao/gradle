package constant

import java.io.File

/**
 * 返回用户图片目录
 */
fun musicDirector(): String {
    val home = File(System.getProperty("user.home"))
    return home.listFiles()?.filter {
        it.name == "picture" || it.name == "图片"
    }?.get(0)?.absolutePath ?: (System.getProperty("user.dir") + sep + "picture")
}

val sep: String = File.separator

/**
 * 列出文件清单,以一个数组形式返回，
 *
 * @param filePath 磁盘文件路径
 * @param fileArr  此参数需要传一个 MutableList<>()进入方法体,在方法体创建一个对象数组，子目录的文件存放不了进数组进行返回
 * @return List<String>
 */
fun cacheFileList(filePath: String, fileArr: MutableList<String>): List<String> {
    val files = File(filePath).listFiles()
    for (k in files.indices) {
        if (files[k].isDirectory) {
            cacheFileList(files[k].path, fileArr)
        } else if (!files[k].isDirectory) {
            fileArr.add(files[k].name)
        }
    }
    return fileArr
}