package io.mzlnk.javalin.xt.routing.internal.processing

/**
 * Represents a generated sources used to register endpoints defined declaratively using javalin-xt.
 *
 * @param adapters list of generated files representing endpoint adapters. See: [EndpointAdapter]
 * @param service generated file representing a service that loads all endpoint adapters factories
 *                using Java Service Provider Interface.
 */
internal data class GeneratedProject(
    val adapters: List<GeneratedFile>,
    val service: GeneratedFile?,
) {

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

}

