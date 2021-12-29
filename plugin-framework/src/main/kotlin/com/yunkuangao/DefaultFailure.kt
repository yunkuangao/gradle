package com.yunkuangao

import com.yunkuangao.util.StringUtils

class DefaultFailure : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(cause: Throwable, message: String, vararg args: Any) : super(StringUtils.format(message, args), cause)

    constructor(message: String, vararg args: Any) : super(StringUtils.format(message, args))

}