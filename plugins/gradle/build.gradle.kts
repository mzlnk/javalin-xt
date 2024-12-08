plugins {
    id("com.gradle.plugin-publish") version "1.3.0"
    `kotlin-dsl`
    `maven-publish`
    signing
}

group = "io.mzlnk"
version = "0.2.0"

repositories {
    mavenCentral()
    google()
}

gradlePlugin {
    website.set("https://github.com/mzlnk/javalin-xt")
    vcsUrl.set("https://github.com/mzlnk/javalin-xt")
    plugins {
        create("javalin-xt-gradle-plugin") {
            id = "io.mzlnk.javalin-xt"
            implementationClass = "io.mzlnk.javalin.xt.plugin.gradle.JavalinXtPlugin"
            displayName = "javalin-xt Gradle Plugin"
            description = "Gradle plugin for seamless integration of javalin-xt into gradle projects."
            tags.set(listOf("javalin-xt", "gradle", "plugin"))
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(gradleApi())

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.25")
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)  // Include all source files
    archiveClassifier.set("sources")   // Name the JAR to be `your-artifact-id-sources.jar`
}

tasks.register<Jar>("javadocJar") {
    from(tasks.getByName("javadoc")) // Include Javadoc task output
    archiveClassifier.set("javadoc")  // Name the JAR to be `your-artifact-id-javadoc.jar`
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            from(components["kotlin"])

            groupId = "io.mzlnk"
            artifactId = "javalin-xt-gradle-plugin"
            version = "0.2.0"

            artifact(tasks.getByName("sourcesJar"))
            artifact(tasks.getByName("javadocJar"))

            pom {
                name.set("javalin-xt-gradle-plugin")
                description.set("Gradle plugin for seamless integration of javalin-xt into gradle projects.")
                url.set("https://github.com/mzlnk/javalin-xt")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("mzlnk")
                        name.set("Marcin Zielonka")
                        email.set("dev.zielonka.marcin@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/mzlnk/javalin-xt.git")
                    developerConnection.set("scm:git:ssh://github.com/mzlnk/javalin-xt.git")
                    url.set("https://github.com/mzlnk/javalin-xt")
                }
            }
        }
    }

    repositories {
        maven {
            name = "local"
            url = uri("${System.getProperty("user.home")}/.m2/repository")
        }
        if (project.version.toString().endsWith("SNAPSHOT")) {
            maven {
                name = "snapshots"
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }
        if (!project.version.toString().endsWith("SNAPSHOT")) {
            maven {
                name = "releases"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }
}

signing {
    // only sign when publishing to maven central
    setRequired(!isSigningForMavenLocal)

    useInMemoryPgpKeys(
        /* defaultKeyId = */ System.getenv("SIGNING_KEY_ID"),
        /* defaultSecretKey = */ System.getenv("SIGNING_KEY"),
        /* defaultPassword = */ System.getenv("SIGNING_PASSWORD")
    )
    sign(publishing.publications["plugin"])
}


private val isSigningForMavenLocal
    get() : Boolean = gradle.startParameter.taskNames.any { it.contains("ToMavenLocal") }

tasks.named<Jar>("jar") {
    archiveBaseName.set("javalin-xt-gradle-plugin")
    archiveVersion.set("")
}
