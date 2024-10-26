package io.mzlnk.javalin.di.internal.ksp

import com.google.devtools.ksp.processing.Resolver
import io.mzlnk.javalin.di.internal.processing.Clazz
import io.mzlnk.javalin.di.internal.processing.Project
import io.mzlnk.javalin.di.internal.processing.ProjectSource

internal class ResolverProjectSource(
    private val resolver: Resolver
) : ProjectSource {

    override fun read(): Project {
        TODO()
    }

}