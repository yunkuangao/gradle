@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
    `java-library`
}

version = "0.1.0"

dependencies {
    implementation(libs.selenium)
    implementation(libs.webdrivermanager)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    modularity.inferModulePath.set(true)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
