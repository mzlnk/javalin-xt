package io.mzlnk.javalin.di.internal.definition

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal interface ClazzSource {

    fun read(): Collection<Clazz>

}

