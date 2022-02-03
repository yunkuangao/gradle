rootProject.name = "gradle"

include("pf4j-demo")
include("pf4j-demo:api")
include("pf4j-demo:app")
include("pf4j-demo:plugins")
include("pf4j-demo:plugins:hello")
include("editor-kotlin")
include("chevereto-kotlin")
include("funkwhale-kotlin")
include("tool-kotlin")
include("tableinfo-word")

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {

    val kotlinVersion = extra["kotlin.version"] as String
    val composeVersion = extra["compose.version"] as String

    pluginManagement {
        repositories {
            mavenLocal()
            google()
            gradlePluginPortal()
            mavenCentral()
            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
            maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }

    }

    versionCatalogs {

        repositories {
            mavenLocal()
            google()
            gradlePluginPortal()
            mavenCentral()
            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
            maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }

        create("libs") {

            // version
            version("kotlin", "1.6.10")
            version("ktor", "1.6.7")
            version("kotlin-logging", "1.12.5")
            version("clikt", "3.4.0")
            version("selenium", "4.1.1")
            version("webdriver", "5.0.3")
            version("klaxon", "5.5")
            version("log4j", "2.17.1")
            version("pf4j", "3.6.0")
            version("commons-lang3", "3.12.0")
            version("slf4j", "1.7.35")
            version("poi-tl", "1.11.1")
            version("mariadb", "2.1.2")
            version("mysql", "8.0.27")

            // plugin
            alias("compose").toPluginId("org.jetbrains.compose").version("1.1.0-alpha1-dev550")
            alias("jvm").toPluginId("org.jetbrains.kotlin.jvm").version(kotlinVersion)
            alias("plugin-serialization").toPluginId("org.jetbrains.kotlin.plugin.serialization").version(kotlinVersion)
            alias("kapt").toPluginId("org.jetbrains.kotlin.kapt").version(kotlinVersion)

            // implementation
            alias("ktor-server-core").to("io.ktor", "ktor-server-core").versionRef("ktor")
            alias("ktor-server-netty").to("io.ktor", "ktor-server-netty").versionRef("ktor")
            alias("ktor-client-core").to("io.ktor", "ktor-client-core").versionRef("ktor")
            alias("ktor-client-cio").to("io.ktor", "ktor-client-cio").versionRef("ktor")
            alias("ktor-client-serialization").to("io.ktor", "ktor-client-serialization").versionRef("ktor")
            alias("ktor-freemarker").to("io.ktor", "ktor-freemarker").versionRef("ktor")
            alias("ktor-serialization").to("io.ktor", "ktor-serialization").versionRef("ktor")
            alias("webdrivermanager").to("io.github.bonigarcia", "webdrivermanager").versionRef("webdriver")
            alias("kotlin-logging").to("io.github.microutils", "kotlin-logging").versionRef("kotlin-logging")

            alias("selenium").to("org.seleniumhq.selenium", "selenium-java").versionRef("selenium")
            alias("slf4j-simple").to("org.slf4j", "slf4j-simple").versionRef("slf4j")
            alias("slf4j-log4j12").to("org.slf4j", "slf4j-log4j12").versionRef("slf4j")
            alias("log4j-api").to("org.apache.logging.log4j", "log4j-api").versionRef("log4j")
            alias("log4j-core").to("org.apache.logging.log4j", "log4j-core").versionRef("log4j")
            alias("commons-lang3").to("org.apache.commons", "commons-lang3").versionRef("commons-lang3")
            alias("pf4j").to("org.pf4j", "pf4j").versionRef("pf4j")
            alias("mariadb").to("org.mariadb.jdbc", "mariadb-java-client").versionRef("mariadb")

            alias("kotlin-reflect").to("org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            alias("kotlin-stdlib").to("org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")

            alias("clikt").to("com.github.ajalt.clikt", "clikt").versionRef("clikt")
            alias("klaxon").to("com.beust", "klaxon").versionRef("klaxon")
            alias("poi-tl").to("com.deepoove", "poi-tl").versionRef("poi-tl")

            alias("mysql").to("mysql", "mysql-connector-java").versionRef("mysql")

            bundle("ktor-server", listOf("ktor-server-core", "ktor-server-netty", "ktor-serialization"))
            bundle("ktor-client", listOf("ktor-client-core", "ktor-client-cio", "ktor-client-serialization"))


            // testImplementation
            alias("kotlin-test").to("org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")
        }
    }
}
