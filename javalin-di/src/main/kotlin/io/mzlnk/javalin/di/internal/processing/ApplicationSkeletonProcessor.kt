package io.mzlnk.javalin.di.internal.processing

import io.mzlnk.javalin.di.internal.processing.file.MetaInfFileGenerator
import io.mzlnk.javalin.di.internal.processing.file.RunnerFileGenerator

internal object ApplicationSkeletonProcessor {

    fun process(project: Project): ApplicationSkeleton {
        val files = listOf(RunnerFileGenerator, MetaInfFileGenerator).map { it.generate(project) }

        return ApplicationSkeleton(
            generatedFiles = files
        )
    }

}