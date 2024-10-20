import org.gradle.api.JavaVersion.VERSION_17

plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
}

group = "io.mzlnk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["java"])

            groupId = "io.mzlnk"
            artifactId = "javalin-di-core"
            version = "1.0-SNAPSHOT"
        }
    }

    repositories {
        maven {
            name = "local" // Name the repository
            url = uri("${System.getProperty("user.home")}/.m2/repository") // Define a local directory to publish the artifact
        }
    }
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
}

