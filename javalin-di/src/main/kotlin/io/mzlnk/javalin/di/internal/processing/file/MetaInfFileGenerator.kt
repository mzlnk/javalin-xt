package io.mzlnk.javalin.di.internal.processing.file

import io.mzlnk.javalin.di.internal.processing.ApplicationSkeleton
import io.mzlnk.javalin.di.internal.processing.Project

internal object MetaInfFileGenerator : FileGenerator {

    override fun generate(project: Project): ApplicationSkeleton.File {
        return ApplicationSkeleton.File(
            name = "META-INF/services/io.mzlnk.javalin.di.spi.JavalinRunnerProvider",
            extension = "",
            content = "${project.rootPackageName}.JavalinRunnerProviderImpl"
        )
    }

}