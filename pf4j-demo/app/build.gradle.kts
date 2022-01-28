val pluginsDir: File by project.parent!!.extra
val appMainClass = "com.yunkuangao.BootKt"

plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":pf4j-demo:api"))
    implementation("org.apache.logging.log4j", "log4j-api")
    implementation("org.apache.logging.log4j", "log4j-core")
    implementation("org.slf4j", "slf4j-log4j12")
    implementation("org.pf4j", "pf4j")
    implementation("org.apache.commons", "commons-lang3")
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
