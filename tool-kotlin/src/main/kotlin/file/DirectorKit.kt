package file

import java.io.File

/**
 * 返回用户音乐目录
 */
val musicDirectory = getDirectory(UserDirectory("music", listOf("music", "音乐")))

/**
 * 返回用户图片目录
 */
val pictureDirectory = getDirectory(UserDirectory("picture", listOf("picture", "图片")))

/**
 * 返回目录
 */
fun getDirectory(directory: UserDirectory): String = File(System.getProperty("user.home"))
    .listFiles()
    ?.filter { directory.directory.contains(it.name) }
    ?.get(0)
    ?.absolutePath ?: (System.getProperty("user.dir") + sep + directory.default)

/**
 * @property default 默认目录
 * @property directors 目录列表
 */
data class UserDirectory(val default: String, val directory: List<String>)

/**
 * 目录分隔符
 */
val sep: String = File.separator

/**
 * 列出文件清单,以一个数组形式返回，
 *
 * @param filePath 磁盘文件路径
 * @param fileArr  此参数需要传一个 MutableList<>()进入方法体,在方法体创建一个对象数组，子目录的文件存放不了进数组进行返回
 * @return List<String>
 */
fun cacheFileList(filePath: String, fileArr: MutableList<String>): List<String> {

    // 保证文件夹存在，若不存在则创建
    existDirectory(filePath)

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