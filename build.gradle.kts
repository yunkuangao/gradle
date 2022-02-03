@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.plugin.serialization) apply false
    alias(libs.plugins.kapt) apply false
}

subprojects {

    group = "com.yunkuangao"
    version = "0.1.0"

    tasks.withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
