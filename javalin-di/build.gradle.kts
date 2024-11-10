import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "_"
    `maven-publish`
    jacoco
    idea
}

group = "io.mzlnk"
version = "0.0.1"

repositories {
    mavenCentral()
}

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

val e2eTestImplementation = configurations["e2eTestImplementation"]

configurations.create("cucumberRuntime") {
    extendsFrom(configurations["e2eTestImplementation"])
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

    e2eTestImplementation("io.cucumber:cucumber-java:_")
    e2eTestImplementation("io.cucumber:cucumber-core:_")
    e2eTestImplementation("io.cucumber:cucumber-expressions:_")
    e2eTestImplementation("org.assertj:assertj-core:_")
}

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

