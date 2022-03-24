import org.jetbrains.compose.compose

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
}

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.logging)
    implementation(project(":tool-kotlin"))
}