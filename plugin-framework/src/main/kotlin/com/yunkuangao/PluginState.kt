package com.yunkuangao

enum class PluginState(private val status: String) {

    CREATED("CREATED"),

    DISABLED("DISABLED"),

    RESOLVED("RESOLVED"),

    STARTED("STARTED"),

    STOPPED("STOPPED"),

    FAILED("FAILED");

    open fun equals(status: String): Boolean {
        return this.status.equals(status, ignoreCase = true)
    }

    override fun toString(): String {
        return status
    }

    companion object {
        fun parse(string: String): PluginState {
            for (status in values()) {
                if (status.equals(string)) {
                    return status
                }
            }
            throw NotFoundException("the $string not found")
        }
    }
}