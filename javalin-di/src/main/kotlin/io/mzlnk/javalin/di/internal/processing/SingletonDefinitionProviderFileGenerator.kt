package io.mzlnk.javalin.di.internal.processing

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.definition.SingletonDefinitionProvider
import io.mzlnk.javalin.di.type.TypeReference

// TODO: refactor it
internal object SingletonDefinitionProviderFileGenerator {

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
