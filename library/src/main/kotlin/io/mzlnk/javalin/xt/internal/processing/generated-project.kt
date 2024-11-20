package io.mzlnk.javalin.xt.internal.processing

/**
 * Represents a generated sources used to create a context by Javalin DI.
 *
 * @param definitionProviders list of generated files representing singleton definition providers.
 *                            See: [SingletonDefinitionProvider]
 * @param definitionProviderService generated file representing a service that loads all definition providers
 *                                  using Java Service Provider Interface.
 */
internal data class GeneratedProject(
    val definitionProviders: List<GeneratedFile>,
    val definitionProviderService: GeneratedFile?,
)

/**
 * Represents a generated file.
 *
 * @param name name of the file
 * @param extension extension of the file
 * @param packageName package name if the file is located in a package, null otherwise
 * @param content content of the file
 */
internal data class GeneratedFile(
    val name: String,
    val extension: String,
    val packageName: String? = null,
    val content: String
)