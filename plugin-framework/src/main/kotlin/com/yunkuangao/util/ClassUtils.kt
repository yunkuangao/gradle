package com.yunkuangao.util

import com.yunkuangao.NotFoundException
import java.lang.annotation.AnnotationTypeMismatchException
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.TypeElement

class ClassUtils {

    companion object {

        fun getAllInterfacesNames(aClass: Class<*>): MutableList<String> {
            return toString(getAllInterfaces(aClass))
        }

        fun getAllInterfaces(aClass: Class<*>): MutableList<Class<*>> {
            var aClass = aClass
            val list: MutableList<Class<*>> = mutableListOf()
            while (aClass != null) {
                val interfaces = aClass.interfaces
                for (anInterface in interfaces) {
                    if (!list.contains(anInterface)) {
                        list.add(anInterface)
                    }
                    val superInterfaces = getAllInterfaces(anInterface)
                    for (superInterface in superInterfaces) {
                        if (!list.contains(superInterface)) {
                            list.add(superInterface)
                        }
                    }
                }
                aClass = aClass.superclass
            }
            return list
        }

        fun getAnnotationMirror(typeElement: TypeElement, annotationClass: Class<*>): AnnotationMirror {
            val annotationClassName = annotationClass.name
            for (m in typeElement.annotationMirrors) {
                if (m.annotationType.toString() == annotationClassName) {
                    return m
                }
            }
            throw NotFoundException("annotationClassName not found")
        }

        fun getAnnotationValue(annotationMirror: AnnotationMirror, annotationParameter: String): AnnotationValue {
            for ((key, value) in annotationMirror.elementValues) {
                if (key!!.simpleName.toString() == annotationParameter) {
                    return value
                }
            }
            throw RuntimeException("annotation not match")
        }

        fun getAnnotationValue(typeElement: TypeElement, annotationClass: Class<*>, annotationParameter: String): AnnotationValue {
            val annotationMirror = getAnnotationMirror(typeElement, annotationClass)
            return if (annotationMirror != null) getAnnotationValue(annotationMirror, annotationParameter)
            else throw RuntimeException("annotation not match")
        }

        private fun toString(classes: MutableList<Class<*>>): MutableList<String> {
            val list: MutableList<String> = mutableListOf()
            for (aClass in classes) {
                list.add(aClass.simpleName)
            }
            return list
        }
    }
}