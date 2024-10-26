package io.mzlnk.javalin.di.internal.processing

internal data class Project(
    val mainFunctionPackageName: String,
    val classes: List<Clazz>
)