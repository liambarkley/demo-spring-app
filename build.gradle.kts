plugins {
    java
    id("org.springframework.boot") version "4.1.2"
}

group = "com.buildcache"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // BOM import, not the separate io.spring.dependency-management plugin - one less plugin
    // version to track, and Gradle's native platform() support handles this fine on its own.
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.2"))
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.2"))

    // spring-boot-starter-web was renamed spring-boot-starter-webmvc in Spring Boot 4 - the
    // old name still resolves but is deprecated, so using the new one from the start.
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // Spring Boot 4 modularized test auto-configuration - @DataJpaTest now needs its own
    // starter, not spring-boot-starter-test alone.
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    // Lets @DataJpaTest run against an embedded database instead of real Postgres - CI builds
    // this way without needing a live database just to compile and test.
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// The plain (non-executable) jar the Spring Boot plugin produces alongside the real bootable
// one isn't used anywhere here - disabling it keeps build/libs/*.jar unambiguous for the
// Dockerfile's wildcard COPY, instead of needing to exclude it by name.
tasks.named<Jar>("jar") {
    enabled = false
}
