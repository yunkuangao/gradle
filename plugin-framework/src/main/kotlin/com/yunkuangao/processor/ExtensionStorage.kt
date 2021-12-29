package com.yunkuangao.processor

import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.util.regex.Pattern
import javax.annotation.processing.Filer
import javax.lang.model.element.Element

abstract class ExtensionStorage(val processor:ExtensionAnnotationProcessor) {

    abstract fun read(): MutableMap<String, MutableSet<String>>

    abstract fun write(extensions: MutableMap<String, MutableSet<String>>)

    protected open fun getFiler(): Filer {
        return processor.getProcessingEnvironment().getFiler()
    }

    protected open fun error(message: String, vararg args: Any) {
        processor.error(message, args)
    }

    protected open fun error(element: Element, message: String, vararg args: Any) {
        processor.error(element, message, args)
    }

    protected open fun info(message: String, vararg args: Any) {
        processor.info(message, args)
    }

    protected open fun info(element: Element, message: String, vararg args: Any) {
        processor.info(element, message, args)
    }

    companion object{
        private val COMMENT = Pattern.compile("#.*")
        private val WHITESPACE = Pattern.compile("\\s+")

        fun read(reader: Reader, entries: MutableSet<String>) {
            BufferedReader(reader).use { bufferedReader ->
                var line: String
                while (bufferedReader.readLine().also { line = it } != null) {
                    line = COMMENT.matcher(line).replaceFirst("")
                    line = WHITESPACE.matcher(line).replaceAll("")
                    if (line.isNotEmpty()) {
                        entries.add(line)
                    }
                }
            }
        }
    }

}