package io.mzlnk.javalin.di.internal.processing

import com.google.devtools.ksp.processing.Resolver
import io.mzlnk.javalin.di.internal.definition.Clazz
import io.mzlnk.javalin.di.internal.definition.ClazzSource

internal class ResolverClazzSource(
    private val resolver: Resolver
) : ClazzSource {

    override fun read(): Collection<Clazz> {
        TODO()
    }

}