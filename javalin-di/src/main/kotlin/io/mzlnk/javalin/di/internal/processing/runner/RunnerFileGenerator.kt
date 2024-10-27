package io.mzlnk.javalin.di.internal.processing.runner

import io.mzlnk.javalin.di.internal.processing.ApplicationSkeleton
import io.mzlnk.javalin.di.internal.processing.FileGenerator
import io.mzlnk.javalin.di.internal.processing.Project
import io.mzlnk.javalin.di.internal.processing.runner.definition.SingletonDefinitionsLoader
import io.mzlnk.javalin.di.internal.processing.runner.graph.DependencyGraphFactory

internal object RunnerFileGenerator : FileGenerator {

    override fun generate(project: Project): ApplicationSkeleton.GeneratedFile {
        val definitions = SingletonDefinitionsLoader.load(project)
        val dependencyGraph = DependencyGraphFactory.create(definitions)

        // language=kotlin
        val content = """
            |package ${project.rootPackageName}
            |
            |import io.javalin.Javalin
            |import io.javalin.config.JavalinConfig
            |import io.mzlnk.javalin.di.spi.JavalinRunnerProvider
            |
            |class JavalinRunnerProviderImpl : JavalinRunnerProvider {
            |    override fun run(configCustomizer: (JavalinConfig) -> Unit) {
            |        val app = Javalin.create { config ->
            |            configCustomizer(config)
            |        }.start()
            |    }
            |
            |}
            """.trimMargin()

        return ApplicationSkeleton.GeneratedFile(
            name = "JavalinRunnerProviderImpl",
            extension = "kt",
            packageName = project.rootPackageName,
            content = content
        )
    }

}