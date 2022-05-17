@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
    `java-library`
}

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.logging)
    implementation(project(":tool-kotlin"))
}