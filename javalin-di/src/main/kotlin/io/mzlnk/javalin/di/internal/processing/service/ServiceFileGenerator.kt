package io.mzlnk.javalin.di.internal.processing.service

import io.mzlnk.javalin.di.internal.processing.ApplicationSkeleton
import io.mzlnk.javalin.di.internal.processing.FileGenerator
import io.mzlnk.javalin.di.internal.processing.Project

internal object ServiceFileGenerator : FileGenerator {

    override fun generate(project: Project): ApplicationSkeleton.GeneratedFile {
        return ApplicationSkeleton.GeneratedFile(
            name = "META-INF/services/io.mzlnk.javalin.di.spi.JavalinRunnerProvider",
            extension = "",
            content = "${project.rootPackageName}.JavalinRunnerProviderImpl"
        )
    }

}