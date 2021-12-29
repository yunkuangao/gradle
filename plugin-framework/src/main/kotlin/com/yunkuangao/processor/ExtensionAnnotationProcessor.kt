package com.yunkuangao.processor

import com.yunkuangao.Extension
import com.yunkuangao.ExtensionPoint
import com.yunkuangao.util.ClassUtils.Companion.getAnnotationMirror
import com.yunkuangao.util.ClassUtils.Companion.getAnnotationValue
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

class ExtensionAnnotationProcessor : AbstractProcessor() {

    var extensions: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set

    var oldExtensions: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set

    private lateinit var storage: ExtensionStorage
    private var ignoreExtensionPoint = false

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        info("%s init", ExtensionAnnotationProcessor::class.java.name)
        info("Options %s", processingEnv.options)
        initStorage()
        initIgnoreExtensionPoint()
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf("*")
    }

    override fun getSupportedOptions(): Set<String> {
        val options: MutableSet<String> = HashSet()
        options.add(STORAGE_CLASS_NAME)
        options.add(IGNORE_EXTENSION_POINT)
        return options
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        if (roundEnv.processingOver()) {
            return false
        }
        info("Processing @%s", Extension::class.java.getName())
        for (element in roundEnv.getElementsAnnotatedWith(Extension::class.java)) {
            if (element.kind != ElementKind.ANNOTATION_TYPE) {
                processExtensionElement(element)
            }
        }

        // collect nested extension annotations
        val extensionAnnotations: MutableList<TypeElement> = mutableListOf()
        for (annotation in annotations) {
            if (getAnnotationMirror(annotation, Extension::class.java) != null) {
                extensionAnnotations.add(annotation)
            }
        }

        // process nested extension annotations
        for (te in extensionAnnotations) {
            info("Processing @%s", te)
            for (element in roundEnv.getElementsAnnotatedWith(te)) {
                processExtensionElement(element)
            }
        }

        oldExtensions = storage!!.read()
        for ((extensionPoint, value) in oldExtensions) {
            if (extensions.containsKey(extensionPoint)) {
                extensions[extensionPoint]!!.addAll(value!!)
            } else {
                extensions[extensionPoint] = value
            }
        }

        storage!!.write(extensions)
        return false
    }

    fun getProcessingEnvironment(): ProcessingEnvironment {
        return processingEnv
    }

    fun error(message: String, vararg args: Any) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, *args))
    }

    fun error(element: Element, message: String, vararg args: Any) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, *args), element)
    }

    fun info(message: String, vararg args: Any) {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, String.format(message, *args))
    }

    fun info(element: Element, message: String, vararg args: Any) {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, String.format(message, *args), element)
    }

    fun getBinaryName(element: TypeElement): String {
        return processingEnv.elementUtils.getBinaryName(element).toString()
    }

    fun getStorage(): ExtensionStorage {
        return storage
    }

    private fun processExtensionElement(element: Element) {
        // check if @Extension is put on class and not on method or constructor
        if (element !is TypeElement) {
            error(element, "Put annotation only on classes (no methods, no fields)")
            return
        }

        // check if class extends/implements an extension point
        if (!ignoreExtensionPoint && !isExtension(element.asType())) {
            error(element, "%s is not an extension (it doesn't implement ExtensionPoint)", element)
            return
        }
        val extensionElement = element
        val extensionPointElements = findExtensionPoints(extensionElement)
        if (extensionPointElements.isEmpty()) {
            error(element, "No extension points found for extension %s", extensionElement)
            return
        }
        val extension = getBinaryName(extensionElement)
        for (extensionPointElement in extensionPointElements) {
            val extensionPoint = getBinaryName(extensionPointElement)
            val extensionPoints = extensions.computeIfAbsent(extensionPoint
            ) { k: String -> TreeSet() }
            extensionPoints!!.add(extension)
        }
    }

    private fun findExtensionPoints(extensionElement: TypeElement): MutableList<TypeElement> {
        val extensionPointElements: MutableList<TypeElement> = mutableListOf()

        // use extension points, that were explicitly set in the extension annotation
        val annotatedExtensionPoints: AnnotationValue = getAnnotationValue(extensionElement, Extension::class.java, "points")
        val extensionPointClasses = annotatedExtensionPoints.value as MutableList<out AnnotationValue>
        if (extensionPointClasses.isNotEmpty()) {
            for (extensionPointClass in extensionPointClasses) {
                val extensionPointClassName = extensionPointClass.value.toString()
                val extensionPointElement = processingEnv.elementUtils.getTypeElement(extensionPointClassName)
                extensionPointElements.add(extensionPointElement)
            }
        } else {
            val interfaces = extensionElement.interfaces
            for (item in interfaces) {
                val isExtensionPoint = processingEnv.typeUtils.isSubtype(item, getExtensionPointType())
                if (isExtensionPoint) {
                    extensionPointElements.add(getElement(item))
                }
            }

            // search in superclass
            val superclass = extensionElement.superclass
            if (superclass.kind != TypeKind.NONE) {
                val isExtensionPoint = processingEnv.typeUtils.isSubtype(superclass, getExtensionPointType())
                if (isExtensionPoint) {
                    extensionPointElements.add(getElement(superclass))
                }
            }

            // pickup the first interface
            if (extensionPointElements.isEmpty() && ignoreExtensionPoint) {
                if (interfaces.isEmpty()) {
                    error(extensionElement, "Cannot use %s as extension point with %s compiler arg (it doesn't implement any interface)",
                        extensionElement, IGNORE_EXTENSION_POINT)
                } else if (interfaces.size == 1) {
                    extensionPointElements.add(getElement(interfaces[0]))
                } else {
                    error(extensionElement, "Cannot use %s as extension point with %s compiler arg (it implements multiple interfaces)",
                        extensionElement, IGNORE_EXTENSION_POINT)
                }
            }
        }
        return extensionPointElements
    }

    private fun isExtension(typeMirror: TypeMirror): Boolean {
        return processingEnv.typeUtils.isAssignable(typeMirror, getExtensionPointType())
    }

    private fun getExtensionPointType(): TypeMirror {
        return processingEnv.elementUtils.getTypeElement(ExtensionPoint::class.java.getName()).asType()
    }

    private fun initStorage() {
        // search in processing options
        var storageClassName = processingEnv.options[STORAGE_CLASS_NAME]
        if (storageClassName == null) {
            // search in system properties
            storageClassName = System.getProperty(STORAGE_CLASS_NAME)
        }
        if (storageClassName != null) {
            // use reflection to create the storage instance
            try {
                val storageClass = javaClass.classLoader.loadClass(storageClassName)
                val constructor = storageClass.getConstructor(ExtensionAnnotationProcessor::class.java)
                storage = constructor.newInstance(this) as ExtensionStorage
            } catch (e: Exception) {
                error(e.message + "")
            }
        }
        if (storage == null) {
            // default storage
            storage = LegacyExtensionStorage(this)
        }
    }

    private fun initIgnoreExtensionPoint() {
        // search in processing options and system properties
        ignoreExtensionPoint = getProcessingEnvironment().options.containsKey(IGNORE_EXTENSION_POINT) ||
                System.getProperty(IGNORE_EXTENSION_POINT) != null
    }

    private fun getElement(typeMirror: TypeMirror): TypeElement {
        return (typeMirror as DeclaredType).asElement() as TypeElement
    }

    companion object {
        private const val STORAGE_CLASS_NAME = "pf4j.storageClassName"
        private const val IGNORE_EXTENSION_POINT = "pf4j.ignoreExtensionPoint"
    }

}