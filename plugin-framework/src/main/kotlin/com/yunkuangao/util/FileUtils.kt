package com.yunkuangao.util

import com.yunkuangao.NotFoundException
import com.yunkuangao.util.StringUtils.Companion.addStart
import mu.KotlinLogging
import java.io.*
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

class FileUtils {

    companion object {

        private val logger = KotlinLogging.logger {}

        fun readLines(path: Path, ignoreComments: Boolean): MutableList<String> {
            val file = path.toFile()
            if (!file.isFile) {
                return mutableListOf()
            }
            val lines: MutableList<String> = mutableListOf()
            BufferedReader(FileReader(file)).use { reader ->
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    if (ignoreComments && !line.startsWith("#") && !lines.contains(line)) {
                        lines.add(line)
                    }
                }
            }
            return lines
        }

        @Deprecated("", ReplaceWith("writeLines(lines, file.toPath())"))
        fun writeLines(lines: Collection<String>, file: File) {
            writeLines(lines, file.toPath())
        }

        fun writeLines(lines: Collection<String>, path: Path) {
            Files.write(path, lines, StandardCharsets.UTF_8)
        }

        fun delete(path: Path) {
            Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
                override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (!attrs.isSymbolicLink) {
                        Files.delete(path)
                    }
                    return FileVisitResult.CONTINUE
                }

                /**
                 * @param exc return null if success,or IOException if error
                 */
                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }
            })
        }

        fun getJars(folder: Path): MutableList<File> {
            val bucket: MutableList<File> = mutableListOf()
            getJars(bucket, folder)
            return bucket
        }

        private fun getJars(bucket: MutableList<File>, folder: Path) {
            val jarFilter: FileFilter = JarFileFilter()
            val directoryFilter: FileFilter = DirectoryFileFilter()
            if (Files.isDirectory(folder)) {
                val jars = folder.toFile().listFiles(jarFilter)
                run {
                    var i = 0
                    while (jars != null && i < jars.size) {
                        bucket.add(jars[i])
                        ++i
                    }
                }
                val directories = folder.toFile().listFiles(directoryFilter)
                var i = 0
                while (directories != null && i < directories.size) {
                    val directory = directories[i]
                    getJars(bucket, directory.toPath())
                    ++i
                }
            }
        }

        fun findWithEnding(basePath: Path, vararg endings: String): Path {
            for (ending in endings) {
                val newPath = basePath.resolveSibling(basePath.fileName.toString() + ending)
                if (Files.exists(newPath)) {
                    return newPath
                }
            }
            throw FileNotFoundException("the ending $endings's files not found")
        }

        fun optimisticDelete(path: Path) {
            try {
                Files.delete(path)
            } catch (ignored: IOException) {
                // ignored
            }
        }

        fun expandIfZip(filePath: Path): Path {
            if (!isZipFile(filePath)) {
                return filePath
            }
            val pluginZipDate = Files.getLastModifiedTime(filePath)
            val fileName = filePath.fileName.toString()
            val directoryName = fileName.substring(0, fileName.lastIndexOf("."))
            val pluginDirectory = filePath.resolveSibling(directoryName)
            if (!Files.exists(pluginDirectory) || pluginZipDate.compareTo(Files.getLastModifiedTime(pluginDirectory)) > 0) {
                // expand '.zip' file
                val unzip = Unzip()
                unzip.setSource(filePath.toFile())
                unzip.setDestination(pluginDirectory.toFile())
                unzip.extract()
                logger.info("Expanded plugin zip '{}' in '{}'", filePath.fileName, pluginDirectory.fileName)
            }
            return pluginDirectory
        }

        fun isZipFile(path: Path): Boolean {
            return Files.isRegularFile(path) && path.toString().lowercase(Locale.getDefault()).endsWith(".zip")
        }

        fun isJarFile(path: Path): Boolean {
            return Files.isRegularFile(path) && path.toString().lowercase(Locale.getDefault()).endsWith(".jar")
        }

        fun isZipOrJarFile(path: Path): Boolean {
            return isZipFile(path) || isJarFile(path)
        }

        fun getPath(path: Path, first: String, vararg more: String): Path {
            var uri = path.toUri()
            if (isZipOrJarFile(path)) {
                var pathString = path.toAbsolutePath().toString()
                // transformation for Windows OS
                pathString = addStart(pathString.replace("\\", "/"), "/")
                // space is replaced with %20
                pathString = pathString.replace(" ", "%20")
                uri = URI.create("jar:file:$pathString")
            }
            return getPath(uri, first, *more)
        }

        fun getPath(uri: URI, first: String, vararg more: String): Path {
            return getFileSystem(uri).getPath(first, *more)
        }

        fun closePath(path: Path) {
            try {
                path.fileSystem.close()
            } catch (e: Exception) {
                // close silently
            }
        }

        fun findFile(directoryPath: Path, fileName: String): Path {
            return File(directoryPath.toUri())
                .walk()
                .maxDepth(3)
                .filter { it.isFile }
                .filter { it.name == fileName }
                .first().toPath()
                ?: throw NotFoundException("file $fileName not found")
        }

        private fun getFileSystem(uri: URI): FileSystem {
            return try {
                FileSystems.getFileSystem(uri)
            } catch (e: FileSystemNotFoundException) {
                FileSystems.newFileSystem(uri, emptyMap<String, String>())
            }
        }

    }
}