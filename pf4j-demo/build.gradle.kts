val pluginsDir by extra { file("$buildDir/plugins") }

plugins {
    kotlin("jvm")
}

version = "0.1.0"

tasks.named("build") {
    dependsOn(":pf4j-demo:app:uberJar")
}
