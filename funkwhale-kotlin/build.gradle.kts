import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

version = "0.1.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.seleniumhq.selenium", "selenium-java")
    implementation("io.github.bonigarcia", "webdrivermanager")
    implementation("io.ktor", "ktor-client-core")
    implementation("io.ktor", "ktor-client-cio")
    implementation("io.github.microutils", "kotlin-logging")
    implementation("org.slf4j", "slf4j-simple")
    implementation("com.github.ajalt.clikt", "clikt")
    implementation("com.beust", "klaxon")
    implementation("io.ktor", "ktor-client-serialization")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.AppImage, TargetFormat.Exe)
            packageName = "funkwhale-kotlin"
            packageVersion = "1.0.0"
        }
    }
}