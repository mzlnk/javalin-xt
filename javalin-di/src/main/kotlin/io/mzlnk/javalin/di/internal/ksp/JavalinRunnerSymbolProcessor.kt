package io.mzlnk.javalin.di.internal.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import io.mzlnk.javalin.di.internal.processing.ApplicationSkeletonProcessor

class JavalinRunnerSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val project = ResolverProjectLoader.load(resolver) ?: return emptyList()
        val applicationSkeleton = ApplicationSkeletonProcessor.process(project)

        applicationSkeleton.generatedFiles.forEach { file ->
            try {
                codeGenerator.createNewFile(
                    dependencies = Dependencies(aggregating = true),
                    packageName = file.packageName ?: "",
                    fileName = file.name,
                    extensionName = file.extension
                ).write(file.content.toByteArray())
            } catch (ignored: Exception) {
            }
        }

        return emptyList()
    }

}

class JavalinRunnerSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return JavalinRunnerSymbolProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}