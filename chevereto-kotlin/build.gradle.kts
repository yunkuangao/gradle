@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
    application
}

version = "0.1.0"

dependencies {
    implementation(libs.selenium)
    implementation(libs.webdrivermanager)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.logging)
    implementation(libs.clikt)
    implementation(project(":tool-kotlin"))
    implementation(project(":download-kotlin"))
    implementation(project(":selenium-kotlin"))
}

application {
    applicationName = "chevereto"
    mainClass.set("MainKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    modularity.inferModulePath.set(true)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
