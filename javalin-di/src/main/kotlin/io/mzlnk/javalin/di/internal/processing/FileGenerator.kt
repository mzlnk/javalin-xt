package io.mzlnk.javalin.di.internal.processing

internal interface FileGenerator {

    fun generate(project: Project): ApplicationSkeleton.GeneratedFile

}