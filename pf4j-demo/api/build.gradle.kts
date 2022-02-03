@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.pf4j)
}