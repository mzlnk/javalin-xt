plugins {
    id("com.gradle.plugin-publish") version "1.3.0"
    `kotlin-dsl`
    publishing
    signing
}

group = "io.mzlnk"
version = "0.5.0"

repositories {
    mavenCentral()
    google()
}

gradlePlugin {
    website = "https://github.com/mzlnk/javalin-xt"
    vcsUrl = "https://github.com/mzlnk/javalin-xt.git"
    plugins {
        create("javalin-xt-gradle-plugin") {
            id = "io.mzlnk.javalin-xt"
            implementationClass = "io.mzlnk.javalin.xt.plugin.gradle.JavalinXtPlugin"
            displayName = "javalin-xt Gradle Plugin"
            description = "Gradle plugin for seamless integration of javalin-xt into gradle projects."
            tags = listOf("javalin", "javalin-xt")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.25")
}

publishing {
    repositories {
        maven {
            name = "local"
            url = uri("${System.getProperty("user.home")}/.m2/repository")
        }
    }
}

signing {
    // skip signing when publishing to local maven repository
    setRequired(!isSigningForMavenLocal)

    useInMemoryPgpKeys(
        /* defaultKeyId = */ System.getenv("SIGNING_KEY_ID"),
        /* defaultSecretKey = */ System.getenv("SIGNING_KEY"),
        /* defaultPassword = */ System.getenv("SIGNING_PASSWORD")
    )
}

private val isSigningForMavenLocal
    get() : Boolean = gradle.startParameter.taskNames.any { it.contains("ToMavenLocal") }