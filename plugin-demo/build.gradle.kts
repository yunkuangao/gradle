val pluginsDir by extra { file("$buildDir/plugins") }

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
    }
}

plugins {
    kotlin("jvm") version "1.6.10"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

group = "com.yunkuangao"
version = "0.1.0"

tasks.named("build") {
    dependsOn(":plugin-demo:app:uberJar")
}
