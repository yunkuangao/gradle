import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
    id(libs.plugins.compose.get().pluginId)
}

version = "0.1.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.selenium)
    implementation(libs.webdrivermanager)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.logging)
    implementation(libs.clikt)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.AppImage, TargetFormat.Exe)
            packageName = "chevereto-kotlin"
            packageVersion = "1.0.0"
        }
    }
}
