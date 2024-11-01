plugins {
    kotlin("jvm") version "_"
    `maven-publish`
}

group = "io.mzlnk"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["java"])

            groupId = "io.mzlnk"
            artifactId = "javalin-di"
            version = "0.0.1"
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
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:_")
    implementation("com.squareup:kotlinpoet:_")

    compileOnly("io.javalin:javalin:_")

    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
    testImplementation("org.junit.jupiter:junit-jupiter-params:_")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:_")

    testImplementation("org.assertj:assertj-core:_")
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

