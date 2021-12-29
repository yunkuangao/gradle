package com.yunkuangao

class DevelopmentPluginClasspath() : PluginClasspath() {

    init {
        addClassesDirectories(MAVEN.getClassesDirectories())
        addClassesDirectories(GRADLE.getClassesDirectories())
        addClassesDirectories(KOTLIN.getClassesDirectories())
        addClassesDirectories(IDEA.getClassesDirectories())
        addJarsDirectories(MAVEN.getJarsDirectories())
        addJarsDirectories(GRADLE.getJarsDirectories())
        addJarsDirectories(KOTLIN.getJarsDirectories())
        addJarsDirectories(IDEA.getJarsDirectories())
    }

    companion object {
        val MAVEN = PluginClasspath().addClassesDirectories("target/classes").addJarsDirectories("target/lib")

        val GRADLE = PluginClasspath().addClassesDirectories("build/classes/java/main", "build/resources/main")

        val KOTLIN = PluginClasspath().addClassesDirectories("build/classes/kotlin/main", "build/resources/main", "build/tmp/kapt3/classes/main")

        val IDEA = PluginClasspath().addClassesDirectories("out/production/classes", "out/production/resource")
    }
}