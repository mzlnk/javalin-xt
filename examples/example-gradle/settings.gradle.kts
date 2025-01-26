pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    id("de.fayard.refreshVersions") version "0.60.3"
}

rootProject.name = "example-gradle"