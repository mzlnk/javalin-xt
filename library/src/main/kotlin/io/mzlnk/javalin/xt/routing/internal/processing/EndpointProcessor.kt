package io.mzlnk.javalin.xt.routing.internal.processing


/**
 * Processes the project and generates all necessary files for the javalin-xt routing:
 * - EndpointAdapter classes for each defined endpoint
 * - EndpointAdapter.Factory service file for Java SPI purposes
 */
internal interface EndpointProcessor {

    fun process(project: Project): GeneratedProject

    companion object {

        fun createForKotlin(): EndpointProcessor {
            return DefaultEndpointProcessor(KotlinEndpointAdapterFileGenerator)
        }

    }

}

/**
 * Default implementation of the [EndpointProcessor].
 */
private class DefaultEndpointProcessor(
    private val endpointAdapterFileGenerator: EndpointAdapterFileGenerator,
) : EndpointProcessor {

    override fun process(project: Project): GeneratedProject {
        val adapters = project.endpoints.map { endpointAdapterFileGenerator.generate(it) }
        val service = EndpointAdapterServiceFileGenerator.generate(project)

        return GeneratedProject(
            adapters = adapters,
            service = service
        )
    }

}

