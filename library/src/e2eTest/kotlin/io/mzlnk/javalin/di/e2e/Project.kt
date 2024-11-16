package io.mzlnk.javalin.di.e2e

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively

class Project private constructor(val rootDirectory: Path) {

    /**
     * Copies resources from host path into the project under the target path
     * relative to the project root directory
     *
     * @param hostPath path to the resource to copy (e.g. src/e2eTest/resources/resource1.txt)
     * @param targetPath path to the target location in the project (e.g. src/main/resources/resource1.txt)
     */
    fun copyResource(hostPath: Path, targetPath: Path) {
        hostPath.toFile().copyTo(rootDirectory.resolve(targetPath).toFile(), overwrite = true)

        val permissions = Files.getPosixFilePermissions(hostPath)
        Files.setPosixFilePermissions(rootDirectory.resolve(targetPath), permissions)
    }

    fun startApplication(): Application {
        val process = ProcessBuilder("./gradlew", "run")
            .directory(rootDirectory.toFile())
            .start()

        return Application.create(process)
    }

    @OptIn(ExperimentalPathApi::class)
    fun destroy() {
        rootDirectory.deleteRecursively()
    }

    companion object {

        fun initialize(): Project {
            val project = Project(rootDirectory = createTempDirectory())

            project.copyResource(
                hostPath = Path.of("src/e2eTest/resources/files/gradle/build.gradle.kts"),
                targetPath = Path.of("./build.gradle.kts")
            )

            project.copyResource(
                hostPath = Path.of("src/e2eTest/resources/files/gradle/settings.gradle.kts"),
                targetPath = Path.of("./settings.gradle.kts")
            )

            project.copyResource(
                hostPath = Path.of("src/e2eTest/resources/files/gradle/gradlew"),
                targetPath = Path.of("./gradlew")
            )

            project.copyResource(
                hostPath = Path.of("src/e2eTest/resources/files/gradle/gradle-wrapper.jar"),
                targetPath = Path.of("./gradle/wrapper/gradle-wrapper.jar")
            )

            project.copyResource(
                hostPath = Path.of("src/e2eTest/resources/files/gradle/gradle-wrapper.properties"),
                targetPath = Path.of("./gradle/wrapper/gradle-wrapper.properties")
            )

            project.copyResource(
                hostPath = Path.of("build/libs/javalin-di.jar"),
                targetPath = Path.of("./libs/javalin-di.jar")
            )

            println("Project initialized at ${project.rootDirectory}")

            return project
        }

    }

}