package io.mzlnk.javalin.xt.e2e

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively

/**
 * Represents a file in the project
 */
data class ProjectFile(
    /**
     * Path to the file relative to the project root directory
     */
    val path: Path,
    /**
     * Content of the file
     */
    val content: String
)

class Project private constructor(private val rootDirectory: Path) {

    private val environmentVariables = mutableMapOf<String, String>()

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

    /**
     * Creates a file with the given content at the specified path relative to the project root directory
     *
     * @param path path to the file to create (e.g. src/main/resources/resource1.txt)
     * @param content content of the file
     */
    fun createFile(file: ProjectFile) {
        Files.createDirectories(rootDirectory.resolve(file.path).parent)
        Files.writeString(rootDirectory.resolve(file.path), file.content)
    }

    /**
     * Sets an environment variable with the given name and value that will be accessible within the project
     *
     * @param name name of the environment variable
     * @param value value of the environment variable
     */
    fun setEnvironmentVariable(name: String, value: String) {
        environmentVariables[name] = value
    }

    fun startApplication(): Application {
        val process = ProcessBuilder("./gradlew", "run")
            .apply { environment().putAll(environmentVariables) }
            .apply { System.getenv("APP_JVM_ARGS")?.let { environment()["APP_JVM_ARGS"] = it } }
            .directory(rootDirectory.toFile())
            .redirectErrorStream(true)
            .start()

        return Application.create(process)
    }

    fun path(child: Path): Path {
        return rootDirectory.resolve(child)
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
                hostPath = Path.of("build/libs/javalin-xt.jar"),
                targetPath = Path.of("./libs/javalin-xt.jar")
            )

            project.copyResource(
                hostPath = Path.of("src/e2eTest/resources/files/resources/logback.xml"),
                targetPath = Path.of("./src/main/resources/logback.xml")
            )

            println("Project initialized at ${project.rootDirectory}")

            return project
        }

    }

}