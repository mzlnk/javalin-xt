package io.mzlnk.javalin.xt.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin for seamless integration of javalin-xt into gradle projects.
 *
 * The plugin:
 * - applies the KSP plugin
 * - adds required dependencies for javalin-xt
 */
class JavalinXtPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // Apply the KSP plugin as it is required for javalin-xt
        project.pluginManager.apply("com.google.devtools.ksp")

        // Add required dependencies for javalin-xt
        project.dependencies.add("implementation", "io.mzlnk:javalin-xt:0.4.0-SNAPSHOT")
        project.dependencies.add("ksp", "io.mzlnk:javalin-xt:0.4.0-SNAPSHOT")
    }
}