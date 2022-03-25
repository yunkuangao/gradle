@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    compileOnly(project(":pf4j-demo:api"))
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.pf4j)
    compileOnly(libs.kotlin.logging)
    ksp(libs.pf4j)
}