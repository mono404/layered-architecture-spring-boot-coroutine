plugins {
    application
}

application {
    mainClass.set("com.mono.backend.BackendLayeredApplicationKt")
}

dependencies {
    implementation(project(":backend-common"))
    implementation(project(":backend-domain"))
    implementation(project(":backend-service"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
