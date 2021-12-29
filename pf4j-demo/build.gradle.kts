val pluginsDir by extra { file("$buildDir/plugins") }

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "com.yunkuangao"
version = "0.1.0"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

tasks.named("build") {
    dependsOn(":pf4j-demo:app:uberJar")
}
