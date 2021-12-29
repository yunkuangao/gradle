package com.yunkuangao

class ClassLoadingStrategy(private val sources: MutableList<Source>) {

    fun getSources(): MutableList<Source> {
        return sources
    }

    enum class Source {
        PLUGIN, APPLICATION, DEPENDENCIES
    }

    companion object {
        val APD = ClassLoadingStrategy(mutableListOf(Source.APPLICATION, Source.PLUGIN, Source.DEPENDENCIES))

        val ADP = ClassLoadingStrategy(mutableListOf(Source.APPLICATION, Source.DEPENDENCIES, Source.PLUGIN))

        val PAD = ClassLoadingStrategy(mutableListOf(Source.PLUGIN, Source.APPLICATION, Source.DEPENDENCIES))

        val DAP = ClassLoadingStrategy(mutableListOf(Source.DEPENDENCIES, Source.APPLICATION, Source.PLUGIN))

        val DPA = ClassLoadingStrategy(mutableListOf(Source.DEPENDENCIES, Source.PLUGIN, Source.APPLICATION))

        val PDA = ClassLoadingStrategy(mutableListOf(Source.PLUGIN, Source.DEPENDENCIES, Source.APPLICATION))
    }

}