plugins {
    kotlin("jvm") version "2.0.21"
    id("io.mzlnk.javalin-xt") version "1.1.0-SNAPSHOT"
}

group = "io.mzlnk"
version = "1.1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.javalin:javalin:_")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    implementation("com.fasterxml.jackson.core:jackson-databind:_")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:_")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}