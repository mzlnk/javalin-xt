package io.mzlnk.javalin.di.internal.processing

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.definition.SingletonDefinitionProvider

internal object SingletonDefinitionProviderFileGenerator {

    fun generate(module: ModuleClass): GeneratedFile {
        val file = FileSpec.builder(
            packageName = module.type.packageName,
            fileName = "${module.type.name}SingletonDefinitionProvider"
        )
            .addType(
                TypeSpec.classBuilder("${module.type.name}SingletonDefinitionProvider")
                    .addSuperinterface(SingletonDefinitionProvider::class)
                    .addProperty(
                        PropertySpec.builder(
                            name = "module",
                            type = ClassName(module.type.packageName, module.type.name),
                            modifiers = listOf(KModifier.PRIVATE),
                        )
                            .initializer("%T()", ClassName(module.type.packageName, module.type.name))
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            name = "definitions",
                            type = List::class.asClassName()
                                .parameterizedBy(SingletonDefinition::class.asClassName().parameterizedBy(STAR)),
                            modifiers = listOf(KModifier.OVERRIDE)
                        )
                            .initializer(
                                CodeBlock.builder()
                                    .add("listOf(\n")
                                    .indent()
                                    .apply {
                                        module.singletons.forEach { method ->
                                            add("%T(\n", SingletonDefinition::class)
                                            indent()
                                            add(
                                                "type = %T::class.java,\n",
                                                ClassName(method.returnType.packageName, method.returnType.name)
                                            )
                                            add("dependencies = ")
                                            method.parameters
                                                .takeIf { it.isNotEmpty() }
                                                ?.let {
                                                    add("listOf(\n")
                                                    indent()
                                                    method.parameters.forEach { parameter ->
                                                        add("%T::class.java,\n", ClassName(parameter.type.packageName, parameter.type.name))
                                                    }
                                                    unindent()
                                                    add("),\n")
                                                }
                                                ?: add("emptyList(),\n")
                                            add("instanceProvider = {\n")
                                            indent()
                                            add("module.%L(", method.name)
                                            indent()
                                            method.parameters.forEachIndexed { index, parameter ->
                                                add(
                                                    "\nit[%L] as %T",
                                                    index,
                                                    ClassName(parameter.type.packageName, parameter.type.name)
                                                )
                                                if (index < method.parameters.size - 1) add(",")
                                            }
                                            unindent()
                                            if(method.parameters.isNotEmpty()) add("\n")
                                            add(")\n")
                                            unindent()
                                            add("}\n")
                                            unindent()
                                            add("),\n")
                                        }
                                    }
                                    .add(")")
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        return GeneratedFile(
            name = singletonDefinitionProviderQualifiedName(module),
            extension = "kt",
            packageName = module.type.packageName,
            content = file.toString()
        )
    }

}