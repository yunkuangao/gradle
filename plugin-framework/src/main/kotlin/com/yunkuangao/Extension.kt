package com.yunkuangao

import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Inherited
@MustBeDocumented
annotation class Extension(
    val ordinal: Int = 0,
    val points: Array<KClass<out ExtensionPoint>> = [],
    val plugins: Array<String> = [],
)
