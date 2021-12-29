package com.yunkuangao

import com.yunkuangao.util.StringUtils.Companion.format

open class ExtensionRuntimeException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(cause: Throwable, message: String, vararg args: Any) : super(format(message, args), cause)

    constructor(message: String, vararg args: Any) : super(format(message, args))

}