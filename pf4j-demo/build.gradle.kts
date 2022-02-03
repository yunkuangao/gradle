val pluginsDir by extra { file("$buildDir/plugins") }

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
}

version = "0.1.0"

tasks.named("build") {
    dependsOn(":pf4j-demo:app:uberJar")
}
