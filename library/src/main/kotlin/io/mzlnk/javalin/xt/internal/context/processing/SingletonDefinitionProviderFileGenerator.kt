package io.mzlnk.javalin.xt.internal.context.processing

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
import io.mzlnk.javalin.xt.context.TypeReference

// TODO: refactor it
/**
 * Generates a file representing a singleton definition provider classes for the given module.
 *
 * It uses KotlinPoet to generate class file content.
 *
 * The structure of the generated file is as follows:
 * ```
 * [package name]
 *
 * [imports]
 *
 * class [module name]SingletonDefinitionProvider : SingletonDefinitionProvider {
 *
 *   private val module = [module name]()
 *
 *   override val definitions: List<SingletonDefinition<*>> = listOf(
 *     SingletonDefinition(
 *       identifier = SingletonDefinition.Identifier(
 *         typeRef = object : TypeReference<[return type]>() {}
 *       ),
 *       dependencies = listOf(
 *         SingletonDefinition.Identifier(typeRef = object : TypeReference<[parameter type]>() {}),
 *         ...
 *       ),
 *       instanceProvider = {
 *         module.[method name](
 *           it[0] as [parameter type],
 *           ...
 *         )
 *       }
 *     ),
 *     ...
 *   )
 * }
 * ```
 */
internal object SingletonDefinitionProviderFileGenerator {

    /**
     * Generates a file representing a singleton definition provider classes for the given module.
     *
     * @param module module to create the definition provider for
     *
     * @return generated file representing a singleton definition provider class file
     */
    fun generate(module: ModuleClass): GeneratedFile {
        val file = FileSpec.builder(
            packageName = module.type.packageName,
            fileName = "${module.type.name}SingletonDefinitionProvider"
        )
            .addType(
                TypeSpec.classBuilder("${module.type.name}SingletonDefinitionProvider")
                    .addSuperinterface(SingletonDefinitionProvider::class)
                    .addProperty(moduleProperty(module))
                    .addProperty(definitionsProperty(module))
                    .build()
            )
            .build()

        return GeneratedFile(
            name = singletonDefinitionProviderSimpleName(module),
            extension = "kt",
            packageName = module.type.packageName,
            content = file.toString()
        )
    }

    private fun moduleProperty(module: ModuleClass): PropertySpec =
        PropertySpec.builder(
            name = "module",
            type = ClassName(module.type.packageName, module.type.name),
            modifiers = listOf(KModifier.PRIVATE),
        )
            .initializer("%T()", ClassName(module.type.packageName, module.type.name))
            .build()

    private fun definitionsProperty(module: ModuleClass): PropertySpec =
        PropertySpec.builder(
            name = "definitions",
            type = List::class
                .asClassName()
                .parameterizedBy(SingletonDefinition::class.asClassName().parameterizedBy(STAR)),
            modifiers = listOf(KModifier.OVERRIDE)
        )
            .initializer(
                CodeBlock.builder()
                    .add("listOf(\n")
                    .indent()
                    .apply {
                        module.singletons.forEachIndexed { idx, method ->
                            addSingletonDefinitionInstance(method)
                            if (idx < module.singletons.size - 1) add(",")
                            add("\n")
                        }
                    }
                    .unindent()
                    .add(")")
                    .build()
            )
            .build()

    private fun CodeBlock.Builder.addSingletonDefinitionInstance(method: SingletonMethod) =
        this
            .add("%T(\n", SingletonDefinition::class)
            .indent()
            .add("identifier = %T(\n", SingletonDefinition.Identifier::class)
            .indent()
            .add("typeRef = object : %T<%L>() {}\n", TypeReference::class.java, method.returnType.qualifiedName)
            .unindent()
            .add("),\n")
            .add("dependencies = ")
            .apply {
                method.parameters
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        add("listOf(\n")
                        indent()
                        method.parameters.forEach { parameter ->
                            add(
                                "%T(typeRef = object : %T<%L>() {}),\n",
                                SingletonDefinition.Identifier::class.java,
                                TypeReference::class.java,
                                parameter.type.qualifiedName
                            )
                        }
                        unindent()
                        add("),\n")
                    }
                    ?: add("emptyList(),\n")
            }
            .add("instanceProvider = {\n")
            .indent()
            .add("module.%L(", method.name)
            .indent()
            .apply {
                method.parameters.forEachIndexed { index, parameter ->
                    add(
                        "\nit[%L] as %L",
                        index,
                        parameter.type.qualifiedName
                    )
                    if (index < method.parameters.size - 1) add(",")
                }
            }
            .unindent()
            .apply { if (method.parameters.isNotEmpty()) add("\n") }
            .add(")\n")
            .unindent()
            .add("}\n")
            .unindent()
            .add(")")
}
