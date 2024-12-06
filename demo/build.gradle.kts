plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

group = "io.mzlnk"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.javalin:javalin:_")
    implementation("io.mzlnk:javalin-xt:0.1.0")
    ksp("io.mzlnk:javalin-xt:0.1.0")
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