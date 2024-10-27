package io.mzlnk.javalin.di.internal.processing.file

import io.mzlnk.javalin.di.internal.processing.ApplicationSkeleton
import io.mzlnk.javalin.di.internal.processing.Project

internal interface FileGenerator {

    fun generate(project: Project): ApplicationSkeleton.File

}