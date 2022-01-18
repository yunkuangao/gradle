import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.6.7"
val logVersion = "1.12.5"
val slf4jVersion = "1.7.29"

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0-alpha1-dev550"
    application
}

group = "com.yunkuangao"
version = "0.1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.seleniumhq.selenium", "selenium-java", "4.1.1")
    implementation("io.github.bonigarcia", "webdrivermanager", "5.0.3")
    implementation("io.ktor", "ktor-client-core", ktorVersion)
    implementation("io.ktor", "ktor-client-cio", ktorVersion)
    implementation("io.github.microutils", "kotlin-logging", logVersion)
    implementation("org.slf4j", "slf4j-simple", slf4jVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}