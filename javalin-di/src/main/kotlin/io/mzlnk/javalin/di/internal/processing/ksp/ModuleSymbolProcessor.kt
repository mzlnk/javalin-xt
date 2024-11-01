package io.mzlnk.javalin.di.internal.processing.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import io.mzlnk.javalin.di.internal.processing.DefaultSingletonDefinitionProcessor
import io.mzlnk.javalin.di.internal.processing.GeneratedFile
import io.mzlnk.javalin.di.internal.processing.SingletonDefinitionProcessor

internal class ModuleSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val processor: SingletonDefinitionProcessor
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val project = ResolverProjectLoader.load(resolver)
        val generatedProject = processor.process(project)

        generatedProject.definitionProviders.forEach { save(it) }
        generatedProject.definitionProviderService?.let { save(it) }

        return emptyList()
    }

    private fun save(file: GeneratedFile) = try {
        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true),
            packageName = file.packageName ?: "",
            fileName = file.name,
            extensionName = file.extension
        ).write(file.content.toByteArray())
    } catch (ignored: Exception) {
    }

}

internal class ModuleSymbolProcessorProvider(
    private val processor: SingletonDefinitionProcessor = DefaultSingletonDefinitionProcessor
) : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ModuleSymbolProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            processor = processor
        )
    }
}