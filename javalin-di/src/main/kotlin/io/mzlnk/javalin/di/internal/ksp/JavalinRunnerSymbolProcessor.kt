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
        val applicationSkeletonFile = ApplicationSkeletonProcessor.process(project)

        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = true),
                packageName = applicationSkeletonFile.packageName,
                fileName = applicationSkeletonFile.fileName,
            ).write(applicationSkeletonFile.content.toByteArray())
        } catch (ignored: Exception) {
        }

        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = "",
                fileName = "META-INF/services/io.mzlnk.javalin.di.spi.JavalinRunnerProvider",
                extensionName = ""
            ).write(
                "${applicationSkeletonFile.packageName}.JavalinRunnerProviderImpl".toByteArray()
            )
        } catch (ignored: Exception) {
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