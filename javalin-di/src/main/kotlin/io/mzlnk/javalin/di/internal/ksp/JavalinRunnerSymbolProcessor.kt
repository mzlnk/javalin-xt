package io.mzlnk.javalin.di.internal.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class JavalinRunnerSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val mainFunction = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filter { it is KSFunctionDeclaration && it.isMain() }
            .map { it as KSFunctionDeclaration }
            .firstOrNull() ?: return emptyList()

        val packageName = mainFunction.packageName.asString()

        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false, mainFunction.containingFile!!),
                packageName = packageName,
                fileName = "JavalinRunner",
            ).write(
                // language=kotlin
                """
            package $packageName

            import io.javalin.Javalin
            import io.javalin.config.JavalinConfig
            import io.mzlnk.javalin.di.spi.JavalinRunnerProvider
            
            class JavalinRunnerProviderImpl : JavalinRunnerProvider {
                override fun run(configCustomizer: (JavalinConfig) -> Unit) {
                    val app = Javalin.create { config ->
                        configCustomizer(config)
                    }.start()
                }
            
            }
            """.trimIndent().toByteArray()
            )
        } catch (ignored: Exception) {
        }

        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = "",
                fileName = "META-INF/services/io.mzlnk.javalin.di.spi.JavalinRunnerProvider",
                extensionName = ""
            ).write(
                "$packageName.JavalinRunnerProviderImpl".toByteArray()
            )
        } catch(ignored: Exception) {}

        return emptyList()
    }

    private fun KSFunctionDeclaration.isMain(): Boolean {
        if (this.simpleName.asString() != "main") {
            return false
        }

        if (this.parentDeclaration != null) {
            return false
        }

        if (this.returnType?.resolve()?.declaration?.qualifiedName?.asString() != "kotlin.Unit") {
            return false
        }

        if (this.parameters.size != 1) {
            return false
        }

        val parameterType = this.parameters[0].type.resolve()
        val parameterTypeDeclaration = parameterType.declaration as? KSClassDeclaration

        if (parameterTypeDeclaration?.qualifiedName?.asString() != "kotlin.Array") {
            return false
        }

        val argumentType = parameterType.arguments.firstOrNull()?.type?.resolve()
        return argumentType?.declaration?.qualifiedName?.asString() == "kotlin.String"
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