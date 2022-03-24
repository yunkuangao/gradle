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
include("selenium-kotlin")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
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

            val kotlinVersion = extra["kotlinVersion"] as String
            val composeVersion = extra["composeVersion"] as String
            val kspVersion = extra["kspVersion"] as String
            val ktorVersion = extra["ktorVersion"] as String
            val kotlinloggingVersion = extra["kotlinloggingVersion"] as String
            val cliktVersion = extra["cliktVersion"] as String
            val seleniumVersion = extra["seleniumVersion"] as String
            val webdriverVersion = extra["webdriverVersion"] as String
            val klaxonVersion = extra["klaxonVersion"] as String
            val pf4jVersion = extra["pf4jVersion"] as String
            val poitlVersion = extra["poitlVersion"] as String
            val mariadbVersion = extra["mariadbVersion"] as String
            val mysqlVersion = extra["mysqlVersion"] as String
            val slf4jVersion = extra["slf4jVersion"] as String

            // version
            version("kotlin", kotlinVersion)
            version("compose", composeVersion)
            version("ksp", kspVersion)
            version("ktor", ktorVersion)
            version("kotlinlogging", kotlinloggingVersion)
            version("clikt", cliktVersion)
            version("selenium", seleniumVersion)
            version("webdriver", webdriverVersion)
            version("klaxon", klaxonVersion)
            version("pf4j", pf4jVersion)
            version("poitl", poitlVersion)
            version("mariadb", mariadbVersion)
            version("mysql", mysqlVersion)
            version("slf4j", slf4jVersion)

            // plugin
            alias("compose").toPluginId("org.jetbrains.compose").versionRef("compose")
            alias("jvm").toPluginId("org.jetbrains.kotlin.jvm").versionRef("kotlin")
            alias("plugin-serialization").toPluginId("org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            alias("ksp").toPluginId("com.google.devtools.ksp").versionRef("ksp")

            // dependency
            alias("kotlin-reflect").to("org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            alias("kotlin-stdlib").to("org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")

            alias("ktor-server-core").to("io.ktor", "ktor-server-core").versionRef("ktor")
            alias("ktor-server-netty").to("io.ktor", "ktor-server-netty").versionRef("ktor")
            alias("ktor-client-core").to("io.ktor", "ktor-client-core").versionRef("ktor")
            alias("ktor-client-cio").to("io.ktor", "ktor-client-cio").versionRef("ktor")
            alias("ktor-client-serialization").to("io.ktor", "ktor-client-serialization").versionRef("ktor")
            alias("ktor-freemarker").to("io.ktor", "ktor-freemarker").versionRef("ktor")
            alias("ktor-serialization").to("io.ktor", "ktor-serialization").versionRef("ktor")

            alias("kotlin-logging").to("io.github.microutils", "kotlin-logging").versionRef("kotlinlogging")
            alias("slf4j-api").to("org.slf4j", "slf4j-api").versionRef("slf4j")
            alias("slf4j-simple").to("org.slf4j", "slf4j-simple").versionRef("slf4j")

            alias("webdrivermanager").to("io.github.bonigarcia", "webdrivermanager").versionRef("webdriver")
            alias("selenium").to("org.seleniumhq.selenium", "selenium-java").versionRef("selenium")

            alias("pf4j").to("org.pf4j", "pf4j").versionRef("pf4j")
            alias("clikt").to("com.github.ajalt.clikt", "clikt").versionRef("clikt")
            alias("klaxon").to("com.beust", "klaxon").versionRef("klaxon")
            alias("poi-tl").to("com.deepoove", "poi-tl").versionRef("poitl")

            alias("mariadb").to("org.mariadb.jdbc", "mariadb-java-client").versionRef("mariadb")
            alias("mysql").to("mysql", "mysql-connector-java").versionRef("mysql")

            // dependency bundle
            bundle("ktor-server", listOf("ktor-server-core", "ktor-server-netty", "ktor-serialization"))
            bundle("ktor-client", listOf("ktor-client-core", "ktor-client-cio", "ktor-client-serialization"))
            bundle("logging", listOf("kotlin-logging", "slf4j-api", "slf4j-simple"))

            // testImplementation
            alias("kotlin-test").to("org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")
        }
    }
}
