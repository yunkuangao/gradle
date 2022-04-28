@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm)
    application
    id("org.beryx.runtime") version "1.12.7"
}

version = "0.1.0"

dependencies {
    implementation(libs.selenium)
    implementation(libs.webdrivermanager)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.logging)
    implementation(libs.clikt)
    implementation(project(":tool-kotlin"))
    implementation(project(":download-kotlin"))
    implementation(project(":selenium-kotlin"))
}

application {
    applicationName = "chevereto"
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


runtime {
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    javaHome.set("C:\\Users\\yun\\.jdks\\openjdk-17.0.2")
    modules.addAll("java.base", "java.logging", "java.xml", "java.instrument")
}

// 附加资源文件
//tasks.runtime.doLast {
//    copy {
//        from('src/main/resources')
//        into("$buildDir/image/bin")
//    }
//}