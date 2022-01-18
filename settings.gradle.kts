pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "yunkuangao"

include("plugin-framework")
include("plugin-demo")
include("plugin-demo:app")
include("plugin-demo:plugins")
include("plugin-demo:plugins:hello")
include("test")
include("spi-kotlin")
include("pf4j-demo")
include("pf4j-demo:api")
include("pf4j-demo:app")
include("pf4j-demo:plugins")
include("pf4j-demo:plugins:hello")
include("editor-kotlin")
include("chevereto-desktop-kotlin")
