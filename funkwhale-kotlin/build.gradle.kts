@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
    application
}

version = "0.1.0"

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.simple)
    implementation(libs.clikt)
    implementation(libs.klaxon)
    implementation(project(":tool-kotlin"))
    implementation(project(":download-kotlin"))
}

application {
    applicationName = "funkwhale"
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
