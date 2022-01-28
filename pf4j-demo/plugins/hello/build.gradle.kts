plugins {
    kotlin("kapt")
}

dependencies {
    compileOnly(project(":pf4j-demo:api"))
    compileOnly(kotlin("stdlib"))

    compileOnly("org.pf4j", "pf4j")
    kapt("org.pf4j", "pf4j")
    implementation("org.apache.commons", "commons-lang3")
}