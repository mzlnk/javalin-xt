package io.mzlnk.javalin.di.internal.processing

import io.mzlnk.javalin.di.internal.processing.content.ApplicationSkeletonContentGenerator
import io.mzlnk.javalin.di.internal.processing.definition.SingletonDefinitionsLoader
import io.mzlnk.javalin.di.internal.processing.graph.DependencyGraphFactory

internal object ApplicationSkeletonProcessor {

    fun process(project: Project): ApplicationSkeleton {
        val content = SingletonDefinitionsLoader.load(project)
            .let { definitions -> DependencyGraphFactory.create(definitions) }
            .let { graph -> ApplicationSkeletonContentGenerator.generate(project.rootPackageName, graph) }

        val runnerFile = ApplicationSkeleton.File(
            name = "JavalinRunnerProviderImpl",
            extension = "kt",
            packageName = project.rootPackageName,
            content = content,
        )

        val metaInfFile = ApplicationSkeleton.File(
            name = "META-INF/services/io.mzlnk.javalin.di.spi.JavalinRunnerProvider",
            extension = "",
            content = "${project.rootPackageName}.JavalinRunnerProviderImpl"
        )

        return ApplicationSkeleton(
            generatedFiles = listOf(runnerFile, metaInfFile)
        )
    }

}