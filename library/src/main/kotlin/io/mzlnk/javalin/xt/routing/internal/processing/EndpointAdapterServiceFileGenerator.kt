package io.mzlnk.javalin.xt.routing.internal.processing

import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
import io.mzlnk.javalin.xt.routing.internal.processing.GeneratedProject.GeneratedFile

/**
 * Generates a service file that contains all qualified names of the endpoint adapter factories.
 * The file is used to load all endpoint adapter factories using Java SPI.
 *
 * The structure of the generated file is as follows:
 * ```
 * [qualified name of endpoint adapter #1 factory]
 * [qualified name of endpoint adapter #2 factory]
 * ...
 * ```
 */
internal object EndpointAdapterServiceFileGenerator {

    /**
     * Generates a service file that contains all qualified names of the endpoint adapter factories.
     *
     * @param project project to generate the service file for
     *
     * @return generated file representing a service file
     */
    fun generate(project: Project): GeneratedFile {
        val content = project.endpoints
            .map { "${it.type.qualifiedName}Adapter\$Factory" }
            .joinToString(separator = "\n") { it }

        return GeneratedFile(
            name = "META-INF/services/io.mzlnk.javalin.xt.routing.generated.EndpointAdapter\$Factory",
            extension = "",
            content = content
        )
    }

}