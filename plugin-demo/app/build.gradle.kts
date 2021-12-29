//val pluginVersion: String by project
val pluginsDir: File by project.parent!!.extra
val appMainClass = "com.yunkuangao.plugindemo.app.BootKt"

plugins {
    kotlin("jvm")
    application
}

val logVersion = "1.12.5"
val slf4jVersion = "1.7.29"
val pluginVersion = "0.1.0"

group = "com.yunkuangao"
version = "0.1.0"

dependencies {
    implementation(kotlin("stdlib"))
//    implementation("com.yunkuangao", "plugin-framework", pluginVersion)
    implementation(project(":plugin-framework"))
    implementation("io.github.microutils", "kotlin-logging", logVersion)
    implementation("org.slf4j", "slf4j-simple", slf4jVersion)
}

application {
    mainClass.set(appMainClass)
}

tasks.named<JavaExec>("run") {
    systemProperty("pf.pluginsDir", pluginsDir.absolutePath)
}

tasks.register<Jar>("uberJar") {
    dependsOn(tasks.named("compileKotlin"))
    archiveClassifier.set("uber")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
//    from({
//        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//    })
    manifest {
        attributes["Main-Class"] = appMainClass
    }

    archiveBaseName.set("${project.name}-plugin-demo")
}
