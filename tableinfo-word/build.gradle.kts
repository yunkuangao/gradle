plugins {
    kotlin("jvm")
    application
    kotlin("plugin.serialization")
}

version = "0.1.0"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor", "ktor-server-core")
    implementation("io.ktor", "ktor-server-netty")
    implementation("io.ktor", "ktor-serialization")
    implementation("com.alibaba", "druid")
    implementation("com.beust", "klaxon")
    implementation("com.deepoove", "poi-tl")
    implementation("mysql", "mysql-connector-java")
    implementation("org.mariadb.jdbc", "mariadb-java-client")
    implementation(project(":tool-kotlin"))
    implementation("org.jetbrains.kotlin", "kotlin-reflect")

    testImplementation(kotlin("test"))
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
