val kotlinVersion = "1.6.10"
val appMainClass = "com.yunkuangao.MainKt"

plugins {
    kotlin("jvm") version "1.6.10"
//    id("jps-compatible")
    application
}

group = "com.yunkuangao"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    api("org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
    api("commons-cli:commons-cli:1.4")
}

application {
    mainClass.set(appMainClass)
}