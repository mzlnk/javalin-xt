package io.mzlnk.javalin.di.internal.processing_v2

data class GeneratedProject(
    val definitionProviders: List<GeneratedFile>,
    val definitionProviderService: GeneratedFile?,
)

data class GeneratedFile(
    val name: String,
    val extension: String,
    val packageName: String? = null,
    val content: String
)