plugins {
    id("java")
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.springframework.boot:spring-boot-starter-web:4.1.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.1.0")
    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
    implementation("org.springframework.boot:spring-boot-starter-security:4.1.0")
    runtimeOnly("org.postgresql:postgresql:42.7.12")

}

tasks.test {
    useJUnitPlatform()
}