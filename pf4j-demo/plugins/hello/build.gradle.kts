plugins {
    kotlin("kapt")
}

val pf4jVersion: String by project

dependencies {
    compileOnly(project(":pf4j-demo:api"))
    compileOnly(kotlin("stdlib"))

    compileOnly("org.pf4j:pf4j:${pf4jVersion}")
    kapt("org.pf4j:pf4j:${pf4jVersion}")
    implementation("org.apache.commons:commons-lang3:3.5")
}