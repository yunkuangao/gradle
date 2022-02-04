@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.ksp.get().pluginId)
}

dependencies {
    compileOnly(project(":pf4j-demo:api"))
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.pf4j)
    compileOnly(libs.kotlin.logging)
    ksp(libs.pf4j)
}