import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.math.floor

plugins {
    kotlin("jvm") version "_"
    `maven-publish`
    signing
    jacoco
    idea
}

group = "io.mzlnk"
version = "0.3.0"

kotlin {
    jvmToolchain(17)
}

sourceSets {
    create("e2eTest") {
        kotlin {
            srcDir("src/e2eTest/kotlin")
        }
        resources {
            srcDir("src/e2eTest/resources")
        }

        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

val e2eTestImplementation = configurations["e2eTestImplementation"]

configurations.create("cucumberRuntime") {
    extendsFrom(configurations["e2eTestImplementation"])
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:_")
    implementation("com.squareup:kotlinpoet:_")

    compileOnly("io.javalin:javalin:_")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:_")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:_")

    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
    testImplementation("org.junit.jupiter:junit-jupiter-params:_")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:_")

    testImplementation("org.assertj:assertj-core:_")
    testImplementation("org.mockito.kotlin:mockito-kotlin:_")
    testImplementation(kotlin("reflect"))

    testImplementation("io.javalin:javalin:_")

    e2eTestImplementation("io.cucumber:cucumber-java:_")
    e2eTestImplementation("io.cucumber:cucumber-core:_")
    e2eTestImplementation("io.cucumber:cucumber-expressions:_")
    e2eTestImplementation("org.assertj:assertj-core:_")
    e2eTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
}

idea {
    module {
        testSources.from(
            sourceSets["e2eTest"].kotlin.srcDirs
        )
        testResources.from(
            sourceSets["e2eTest"].resources.srcDirs
        )
    }
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
        create<MavenPublication>("library") {
            from(components["kotlin"])

            groupId = "io.mzlnk"
            artifactId = "javalin-xt"
            version = "0.3.0"

            artifact(tasks.getByName("sourcesJar"))
            artifact(tasks.getByName("javadocJar"))

            pom {
                name.set("javalin-xt")
                description.set("Simple and very lightweight set of extension features dedicated to Javalin")
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

        if(!project.version.toString().endsWith("-SNAPSHOT")) {
            maven {
                name = "releases"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }

        if(project.version.toString().endsWith("-SNAPSHOT")) {
            maven {
                name = "snapshots"
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

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
    sign(publishing.publications["library"])
}

private val isSigningForMavenLocal
    get() : Boolean = gradle.startParameter.taskNames.any { it.contains("ToMavenLocal") }

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
        csv.required.set(true)
        csv.outputLocation.set(layout.buildDirectory.file("jacocoCsv/jacoco.csv"))
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("jacocoXml/jacoco.xml"))
    }
}

tasks.register("testCoverage") {
    dependsOn("jacocoTestReport")

    doLast {
        val buildDir = layout.buildDirectory.asFile.get()
        val reportFile = buildDir.resolve("jacocoCsv/jacoco.csv")
        if (!reportFile.exists()) {
            throw GradleException("Jacoco report file not found at ${reportFile.absolutePath}")
        }

        val (missed, covered) = reportFile
            .readLines()
            .drop(1) // drop headers
            .map { it.split(",") }
            .map { it[3].toInt() to it[4].toInt() }
            .fold(0 to 0) { (totalMissed, totalCovered), (missed, covered) ->
                totalMissed + missed to totalCovered + covered
            }

        val coverage = floor((covered.toDouble() / (missed + covered)) * 10000) / 100

        val coverageFile = buildDir.resolve("coverage.txt")
        coverageFile.writeText(coverage.toString())

        println("Test coverage: $coverage%")
    }
}

tasks.register("e2eTest") {
    dependsOn("build")
    dependsOn("e2eTestClasses")

    doLast {
        javaexec {
            mainClass = "io.cucumber.core.cli.Main"
            classpath = configurations["cucumberRuntime"] + sourceSets["e2eTest"].runtimeClasspath
        }
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("javalin-xt")
    archiveVersion.set("")
}