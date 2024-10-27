package io.mzlnk.javalin.di.internal.processing

internal data class Project(
    val rootPackageName: String,
    val classes: List<Clazz>
)