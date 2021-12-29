package com.yunkuangao.util

class StringUtils {
    companion object {

        fun format(str: String, vararg args: Any): String {
            var str = str
            str = str.replace("\\{}".toRegex(), "%s")
            return String.format(str, *args)
        }

        fun addStart(str: String, add: String): String {
            if (add.isEmpty()) {
                return str
            }
            if (str.isEmpty()) {
                return add
            }
            return if (!str.startsWith(add)) {
                add + str
            } else str
        }
    }
}
