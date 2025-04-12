dependencies {
    implementation(project(":backend-domain"))
    implementation(project(":backend-common"))

    api("org.springframework.boot:spring-boot-starter-data-r2dbc")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.kafka:spring-kafka")

    runtimeOnly("io.asyncer:r2dbc-mysql:1.3.0")
}