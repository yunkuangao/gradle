@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.kapt.get().pluginId)
}

dependencies {
    compileOnly(project(":pf4j-demo:api"))
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.pf4j)
    kapt(libs.pf4j)
    implementation(libs.commons.lang3)
}