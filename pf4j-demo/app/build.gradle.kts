val pluginsDir: File by project.parent!!.extra
val appMainClass = "com.yunkuangao.BootKt"

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
    application
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.slf4j.log4j12)
    implementation(libs.pf4j)
    implementation(libs.commons.lang3)
    implementation(project(":pf4j-demo:api"))
}

application {
    mainClass.set(appMainClass)
}

tasks.named<JavaExec>("run") {
    systemProperty("pf4j.pluginsDir", pluginsDir.absolutePath)
}

tasks.register<Jar>("uberJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    dependsOn(tasks.named("compileKotlin"))
    archiveClassifier.set("uber")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    manifest {
        attributes["Main-Class"] = appMainClass
    }

    archiveBaseName.set("${project.name}-plugin-demo")
}
