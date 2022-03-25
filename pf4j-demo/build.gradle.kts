val pluginsDir by extra { file("$buildDir/plugins") }

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
}

version = "0.1.0"

tasks.named("build") {
    dependsOn(":pf4j-demo:app:uberJar")
}
