dependencies {
    implementation(project(":backend-common"))
    implementation(project(":backend-domain"))
    implementation(project(":backend-persistence"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
