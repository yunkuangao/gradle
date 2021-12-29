package com.yunkuangao

enum class RuntimeMode(private val named: String, private val aliases: Array<String>) {
    DEVELOPMENT("development", arrayOf("dev")),

    DEPLOYMENT("deployment", arrayOf("prod"));

    override fun toString(): String {
        return named
    }

    companion object {
        private val map: MutableMap<String, RuntimeMode> = mutableMapOf()

        init {
            for (mode in values()) {
                map[mode.named] = mode
                for (alias in mode.aliases) {
                    map[alias] = mode
                }
            }
        }

        fun byNamed(named: String): RuntimeMode {
            if (map.containsKey(named)) {
                return map[named]!!
            }
            throw NoSuchElementException("Cannot found PF4J runtime mode with named '" + named + "'." +
                    "Must be one value from '" + map.keys + ".")
        }
    }

}