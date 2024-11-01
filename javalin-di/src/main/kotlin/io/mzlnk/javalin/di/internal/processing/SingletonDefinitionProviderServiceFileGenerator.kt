package io.mzlnk.javalin.di.internal.processing

internal object SingletonDefinitionProviderServiceFileGenerator {

    fun generate(project: Project): GeneratedFile {
        val content = project.modules
            .map { "${it.type.qualifiedName}SingletonDefinitionProvider" }
            .joinToString(separator = "\n") { it }

        return GeneratedFile(
            name = "META-INF/services/io.mzlnk.javalin.di.definition.SingletonDefinitionProvider",
            extension = "",
            content = content
        )
    }

}