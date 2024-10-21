package io.mzlnk.javalin.di.internal.processing

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.mzlnk.javalin.di.internal.definition.ClasspathClassSource
import io.mzlnk.javalin.di.internal.definition.SingletonDefinitionsLoader
import io.mzlnk.javalin.di.internal.graph.DependencyGraphFactory

internal class JavalinApplicationSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val singletonDefinitionsLoader: SingletonDefinitionsLoader = SingletonDefinitionsLoader(ClasspathClassSource),
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val javalinApplicationAnnotated = resolver.getSymbolsWithAnnotation("io.mzlnk.javalin.di.JavalinApplication").toList()

        if (javalinApplicationAnnotated.size > 1) {
            throw IllegalStateException("Multiple `@JavalinApplication` annotations found")
        }

        if (javalinApplicationAnnotated.isNotEmpty()) {
            // annotation itself enforces that annotated symbol is a kotlin class
            processJavalinApplicationAnnotatedClass(javalinApplicationAnnotated.first() as KSClassDeclaration)
        }

        return emptyList()
    }

    private fun processJavalinApplicationAnnotatedClass(clazz: KSClassDeclaration) {
        val content = singletonDefinitionsLoader.load()
            .let(DependencyGraphFactory::create)
            .let(DependencyGraphClassFileGenerator::generateContent)

        val clazzFile = codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true, clazz.containingFile!!),
            packageName = clazz.packageName.asString(),
            fileName = "JavalinApplication\$Runner",
        )

        clazzFile.write(content.toByteArray())
    }

}

internal class JavalinApplicationSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return JavalinApplicationSymbolProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}
