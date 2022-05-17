@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jvm) apply false
}

subprojects {

    group = "com.yunkuangao"
    version = "0.1.0"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
