package com.yunkuangao

class ExtensionWrapper<T>(
    val descriptor: ExtensionDescriptor<T>,
    private val extensionFactory: ExtensionFactory,
) : Comparable<ExtensionWrapper<T>> {

    var extension: T = extensionFactory.create(descriptor.extensionClass)

    fun getOrdinal(): Int {
        return descriptor.ordinal
    }

    override fun compareTo(other: ExtensionWrapper<T>): Int {
        return getOrdinal() - other.getOrdinal()
    }

}