rootProject.name = "layered-architecture-spring-boot-coroutine"

pluginManagement {
    val springBootVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version "1.1.7"
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include("backend-presentation")
include("backend-service")
include("backend-persistence")
include("backend-domain")
include("backend-common")

rootProject.children.forEach { project ->
    project.buildFileName = "${project.name.lowercase()}.gradle.kts"
    assert(project.buildFile.isFile)
}
