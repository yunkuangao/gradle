@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.jvm.get().pluginId)
    `maven-publish`
    `java-library`
}

version = "0.1.1"

dependencies {
    implementation(libs.kotlin.stdlib)

    testImplementation(libs.kotlin.test)
}

// publish maven repo
val spaceUsername: String by project
val spacePassword: String by project
//
//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = project.group.toString()
//            artifactId = "tool"
//            version = project.version.toString()
//            from(components["java"])
//        }
//    }
//    repositories {
//        maven {
//            url = uri("https://maven.pkg.jetbrains.space/yunkuangao/p/yunkuangao/tool")
//            credentials {
//                username = spaceUsername
//                password = spacePassword
//            }
//        }
//    }
//}