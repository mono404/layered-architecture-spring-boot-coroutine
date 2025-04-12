plugins {
    kotlin("kapt")
}

dependencies {
    val coroutineVersion: String by project
    val jacksonVersion: String by project

    // coroutine
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutineVersion")

    // Log
    api("ch.qos.logback:logback-classic")

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
}
