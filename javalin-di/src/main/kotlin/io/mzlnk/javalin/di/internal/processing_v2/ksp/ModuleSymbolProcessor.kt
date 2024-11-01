package io.mzlnk.javalin.di.internal.processing_v2.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import io.mzlnk.javalin.di.internal.processing_v2.GeneratedFile
import io.mzlnk.javalin.di.internal.processing_v2.SingletonDefinitionProcessor

class ModuleSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val project = ResolverProjectLoader.load(resolver)
        val generatedProject = SingletonDefinitionProcessor.process(project)

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

        // TODO: refactor it (PoC bypass)
//        codeGenerator.createNewFile(
//            dependencies = Dependencies(aggregating = true),
//            packageName = "",
//            fileName = "META-INF/services/io.mzlnk.javalin.di.internal.context.SingletonDefinitionProvider",
//            extensionName = ""
//        ).write("${file.packageName}.${file.name}".toByteArray())
    } catch (ignored: Exception) {
    }

}

class ModuleSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ModuleSymbolProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}