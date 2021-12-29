plugins {
    kotlin("jvm")
    kotlin("kapt")
}

val logVersion = "1.12.5"
val slf4jVersion = "1.7.29"
val pluginVersion = "0.1.0"

group = "com.yunkuangao"
version = "0.1.0"

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(project(":plugin-demo:app"))

    kapt("com.yunkuangao", "plugin-framework", pluginVersion)

//    implementation("com.yunkuangao", "plugin-framework", pluginVersion)
    implementation(project(":plugin-framework"))
    implementation("io.github.microutils", "kotlin-logging", logVersion)
    implementation("org.slf4j", "slf4j-simple", slf4jVersion)

}