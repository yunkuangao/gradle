@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
}

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.logging)
    implementation(project(":tool-kotlin"))
}