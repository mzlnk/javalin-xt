package io.mzlnk.javalin.di.internal.processing

import io.mzlnk.javalin.di.definition.SingletonDefinitionProvider

internal object SingletonDefinitionProviderServiceFileGenerator {

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