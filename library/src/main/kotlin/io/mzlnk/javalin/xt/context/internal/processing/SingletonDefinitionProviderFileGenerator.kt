package io.mzlnk.javalin.xt.context.internal.processing

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mzlnk.javalin.xt.context.TypeReference
import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.context.generated.SingletonDefinitionProvider
import io.mzlnk.javalin.xt.properties.Property

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
    fun generate(module: Module): GeneratedFile {
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

    private fun moduleProperty(module: Module): PropertySpec =
        PropertySpec.builder(
            name = "module",
            type = ClassName(module.type.packageName, module.type.name),
            modifiers = listOf(KModifier.PRIVATE),
        )
            .initializer("%T()", ClassName(module.type.packageName, module.type.name))
            .build()

    private fun definitionsProperty(module: Module): PropertySpec =
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

    private fun CodeBlock.Builder.addSingletonDefinitionInstance(singleton: Singleton) =
        this
            .add("%T(\n", SingletonDefinition::class)
            .indent()
            .add("identifier = %T(\n", SingletonDefinition.Identifier::class)
            .indent()
            .add("name = %L,\n", singleton.name?.let { "\"$it\"" } ?: "null")
            .add("typeRef = object : %T<%L>() {}\n", TypeReference::class.java, singleton.type.qualifiedName)
            .unindent()
            .add("),\n")
            .add("conditions = ")
            .apply {
                singleton.conditionals
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        add("listOf(\n")
                        indent()
                        singleton.conditionals.forEach { conditional ->
                            when (conditional) {
                                is Singleton.Conditional.OnProperty -> {
                                    add(
                                        "%T(property = \"%L\", havingValue = \"%L\"),\n",
                                        SingletonDefinition.Condition.OnProperty::class.java,
                                        conditional.key,
                                        conditional.havingValue
                                    )
                                }
                            }
                        }
                        unindent()
                        add("),\n")
                    }
                    ?: add("emptyList(),\n")
            }
            .add("dependencies = ")
            .apply {
                singleton.dependencies
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        add("listOf(\n")
                        indent()
                        singleton.dependencies.forEach { dependency ->
                            when(dependency) {
                                is Singleton.Dependency.Singleton.Singular -> {
                                    add(
                                        "%T(name = %L, typeRef = object : %T<%L>() {}),\n",
                                        SingletonDefinition.DependencyIdentifier.Singleton.Singular::class.java,
                                        dependency.name?.let { "\"$it\"" } ?: "null",
                                        TypeReference::class.java,
                                        dependency.type.qualifiedName
                                    )
                                }
                                is Singleton.Dependency.Singleton.List -> {
                                    add(
                                        "%T(name = %L, elementName = %L, typeRef = object : %T<%L>() {}),\n",
                                        SingletonDefinition.DependencyIdentifier.Singleton.List::class.java,
                                        dependency.name?.let { "\"$it\"" } ?: "null",
                                        dependency.elementName?.let { "\"$it\"" } ?: "null",
                                        TypeReference::class.java,
                                        dependency.type.qualifiedName
                                    )
                                }
                                is Singleton.Dependency.Property -> {
                                    add(
                                        "%T(key = \"%L\", valueProvider = %T::as%L, required = %L),\n",
                                        SingletonDefinition.DependencyIdentifier.Property::class.java,
                                        dependency.key,
                                        Property::class.java,
                                        when (dependency.type.qualifiedName.substringBefore("?")) {
                                            "kotlin.String" -> "String"
                                            "kotlin.Int" -> "Int"
                                            "kotlin.Double" -> "Double"
                                            "kotlin.Float" -> "Float"
                                            "kotlin.Boolean" -> "Boolean"
                                            "kotlin.collections.List<kotlin.String>" -> "StringList"
                                            "kotlin.collections.List<kotlin.Int>" -> "IntList"
                                            "kotlin.collections.List<kotlin.Double>" -> "DoubleList"
                                            "kotlin.collections.List<kotlin.Float>" -> "FloatList"
                                            "kotlin.collections.List<kotlin.Boolean>" -> "BooleanList"
                                            else -> throw IllegalArgumentException("Unsupported property type: ${dependency.type.qualifiedName}")
                                        },
                                        dependency.required
                                    )
                                }
                            }
                        }
                        unindent()
                        add("),\n")
                    }
                    ?: add("emptyList(),\n")
            }
            .add("instanceProvider = {\n")
            .indent()
            .add("module.%L(", singleton.methodName)
            .indent()
            .apply {
                singleton.dependencies.forEachIndexed { index, parameter ->
                    add(
                        "\nit[%L] as %L",
                        index,
                        parameter.type.qualifiedName
                    )
                    if (index < singleton.dependencies.size - 1) add(",")
                }
            }
            .unindent()
            .apply { if (singleton.dependencies.isNotEmpty()) add("\n") }
            .add(")\n")
            .unindent()
            .add("}\n")
            .unindent()
            .add(")")

}
