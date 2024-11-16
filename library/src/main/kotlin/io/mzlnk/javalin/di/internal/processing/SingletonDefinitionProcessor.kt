package io.mzlnk.javalin.di.internal.processing

/**
 * Processes the project and generates all necessary files for the Javalin DI context:
 * - Singleton definition providers classes for each module
 * - Singleton definition provider service file for Java SPI purposes
 */
internal interface SingletonDefinitionProcessor {

    fun process(project: Project): GeneratedProject

}

/**
 * Default implementation of the [SingletonDefinitionProcessor].
 */
internal object DefaultSingletonDefinitionProcessor : SingletonDefinitionProcessor {

    override fun process(project: Project): GeneratedProject {
        val definitionProviders = project.modules.map { SingletonDefinitionProviderFileGenerator.generate(it) }
        val definitionProviderService = SingletonDefinitionProviderServiceFileGenerator.generate(project)

        return GeneratedProject(
            definitionProviders = definitionProviders,
            definitionProviderService = definitionProviderService
        )
    }

}