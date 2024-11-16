package io.mzlnk.javalin.ext.internal.processing

import io.mzlnk.javalin.ext.definition.SingletonDefinitionProvider

/**
 * Generates a service file that contains all qualified names of the singleton definition providers.
 * The file is used to load all definition providers using Java SPI.
 *
 * The structure of the generated file is as follows:
 * ```
 * [qualified name of singleton definition provider #1]
 * [qualified name of singleton definition provider #2]
 * ...
 * ```
 */
internal object SingletonDefinitionProviderServiceFileGenerator {

    /**
     * Generates a service file that contains all qualified names of the singleton definition providers.
     *
     * @param project project to generate the service file for
     *
     * @return generated file representing a service file
     */
    fun generate(project: Project): GeneratedFile {
        val content = project.modules
            .map { singletonDefinitionProviderQualifiedName(it) }
            .joinToString(separator = "\n") { it }

        return GeneratedFile(
            name = "META-INF/services/${SingletonDefinitionProvider::class.java.canonicalName}",
            extension = "",
            content = content
        )
    }

}