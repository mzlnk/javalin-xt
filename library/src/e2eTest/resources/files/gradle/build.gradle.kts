plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
    application
}

group = "io.mzlnk"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

repositories {
    flatDir {
        dirs("libs")
    }
    mavenCentral()
}

dependencies {
    // load javalin-di from local JAR located at `libs/` directory
    implementation(":javalin-xt")
    ksp(":javalin-xt")
    ksp("com.squareup:kotlinpoet:2.0.0")

    implementation("io.javalin:javalin:6.3.0")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass.set("io.mzlnk.javalin.xt.e2e.app.ApplicationKt")
}