plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("org.jetbrains.compose") version "1.1.0-alpha1-dev550" apply false
}

group = "com.yunkuangao"
version = "0.1.0"

subprojects {
    apply(plugin = "java")

    group = "com.yunkuangao"
    version = "0.1.0"

    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }

    val ktorVersion = "1.6.7"
    val logVersion = "1.12.5"
    val slf4jVersion = "1.7.29"
    val cliktVersion = "3.4.0"
    val seleniumVersion = "4.1.1"
    val webdriverVersion = "5.0.3"
    val klaxonVersion = "5.5"
    val log4jVersion = "2.14.0"
    val pf4jVersion = "3.6.0"
    val commonslang3Version = "3.5"
    val log4j12Version = "1.7.28"

    dependencies {

        "implementation"("io.ktor", "ktor-client-core", ktorVersion)
        "implementation"("io.ktor", "ktor-client-cio", ktorVersion)
        "implementation"("io.ktor", "ktor-client-serialization", ktorVersion)
        "implementation"("io.github.bonigarcia", "webdrivermanager", webdriverVersion)
        "implementation"("io.github.microutils", "kotlin-logging", logVersion)

        "implementation"("org.seleniumhq.selenium", "selenium-java", seleniumVersion)
        "implementation"("org.slf4j", "slf4j-simple", slf4jVersion)
        "implementation"("org.slf4j", "slf4j-log4j12", log4j12Version)
        "implementation"("org.apache.logging.log4j", "log4j-api", log4jVersion)
        "implementation"("org.apache.logging.log4j", "log4j-core", log4jVersion)
        "implementation"("org.apache.commons", "commons-lang3", commonslang3Version)
        "implementation"("org.pf4j", "pf4j", pf4jVersion)

        "implementation"("com.github.ajalt.clikt", "clikt", cliktVersion)
        "implementation"("com.beust", "klaxon", klaxonVersion)
    }
}