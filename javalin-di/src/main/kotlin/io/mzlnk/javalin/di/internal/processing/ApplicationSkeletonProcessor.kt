package io.mzlnk.javalin.di.internal.processing

import io.mzlnk.javalin.di.internal.processing.content.ApplicationSkeletonContentGenerator
import io.mzlnk.javalin.di.internal.processing.definition.SingletonDefinitionsLoader
import io.mzlnk.javalin.di.internal.processing.graph.DependencyGraphFactory

internal object ApplicationSkeletonProcessor {

    fun process(project: Project): ApplicationSkeletonFile {
        val content = SingletonDefinitionsLoader.load(project)
            .let { definitions -> DependencyGraphFactory.create(definitions) }
            .let { graph -> ApplicationSkeletonContentGenerator.generate(project.mainFunctionPackageName, graph) }

        return ApplicationSkeletonFile(
            packageName = project.mainFunctionPackageName,
            fileName = "JavalinRunnerProviderImpl",
            content = content
        )
    }

}