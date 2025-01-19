package io.mzlnk.javalin.xt.routing.internal.processing.inbound.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import io.mzlnk.javalin.xt.routing.internal.processing.EndpointProcessor
import io.mzlnk.javalin.xt.routing.internal.processing.GeneratedProject
import io.mzlnk.javalin.xt.routing.internal.processing.GeneratedProject.GeneratedFile

/**
 * Symbol processor that processes the project source code
 * and generates all necessary files for the Javalin DI context using KSP API.
 */
internal class EndpointSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val processor: EndpointProcessor
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val project = ResolverProjectLoader.load(resolver)
        val generatedProject = processor.process(project)

        generatedProject.adapters.forEach { save(it) }
        generatedProject.service?.let { save(it) }

        return emptyList()
    }

    private fun save(file: GeneratedFile) = try {
        codeGenerator.createNewFile(
            dependencies = Dependencies.ALL_FILES,
            packageName = file.packageName ?: "",
            fileName = file.name,
            extensionName = file.extension
        ).write(file.content.toByteArray())
    } catch (ignored: Exception) {
    }

}

/**
 * Provider for the [EndpointSymbolProcessor]. Required by KSP.
 */
internal class EndpointSymbolProcessorProvider(
    private val processor: EndpointProcessor = EndpointProcessor.createForKotlin()
) : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EndpointSymbolProcessor(
            codeGenerator = environment.codeGenerator,
            processor = processor
        )
    }
}