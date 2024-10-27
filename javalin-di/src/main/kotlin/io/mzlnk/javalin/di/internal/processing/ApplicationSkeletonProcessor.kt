package io.mzlnk.javalin.di.internal.processing

import io.mzlnk.javalin.di.internal.processing.service.ServiceFileGenerator
import io.mzlnk.javalin.di.internal.processing.runner.RunnerFileGenerator

internal object ApplicationSkeletonProcessor {

    fun process(project: Project): ApplicationSkeleton {
        val files = listOf(RunnerFileGenerator, ServiceFileGenerator).map { it.generate(project) }

        return ApplicationSkeleton(
            generatedFiles = files
        )
    }

}