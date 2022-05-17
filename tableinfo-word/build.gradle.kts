@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
    alias(libs.plugins.plugin.serialization)
    application
}

version = "0.1.0"

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.logging)
    implementation(libs.bundles.ktor.server)
    implementation(libs.klaxon)
    implementation(libs.poi.tl)
    implementation(libs.mysql)
    implementation(libs.mariadb)
    implementation(libs.mysql)
    implementation(project(":tool-kotlin"))

    testImplementation(libs.kotlin.test)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(17))
//    }
}