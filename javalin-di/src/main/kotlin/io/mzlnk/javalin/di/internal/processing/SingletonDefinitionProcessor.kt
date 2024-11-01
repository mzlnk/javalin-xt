package io.mzlnk.javalin.di.internal.processing

internal interface SingletonDefinitionProcessor {

    fun process(project: Project): GeneratedProject

}

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