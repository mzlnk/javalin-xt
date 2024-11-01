package io.mzlnk.javalin.di.internal.processing_v2

internal object SingletonDefinitionProviderServiceFileGenerator {

    fun generate(project: Project): GeneratedFile {
        val content = project.modules
            .map { it.type.qualifiedName }
            .joinToString(separator = "\n") { it }

        return GeneratedFile(
            name = "META-INF/services/io.mzlnk.javalin.di.spi.SingletonDefinitionProvider",
            extension = "",
            content = content
        )
    }

}