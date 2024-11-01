package io.mzlnk.javalin.di.internal.processing

internal data class GeneratedProject(
    val definitionProviders: List<GeneratedFile>,
    val definitionProviderService: GeneratedFile?,
)

internal data class GeneratedFile(
    val name: String,
    val extension: String,
    val packageName: String? = null,
    val content: String
)