package io.mzlnk.javalin.di.internal.definition

import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.Named
import io.mzlnk.javalin.di.Singleton

internal class SingletonDefinitionsLoader(
    private val clazzSource: ClazzSource
) {

    fun load(): List<SingletonDefinition> {
        val allClasses = clazzSource.read()

        return allClasses
            .filter { clazz -> clazz.annotations.any { it.isTypeOf(Module::class) } }
            .flatMap { clazz -> clazz.methods.map { method -> clazz to method } }
            .filter { (_, method) -> method.annotations.any { it.isTypeOf(Singleton::class) } }
            .map { (clazz, method) ->
                SingletonDefinition(
                    key = SingletonDefinition.Key(
                        type = method.returnType,
                        name = method.annotations.find { it.isTypeOf(Named::class) }?.arguments?.find { it.name == "value" }?.value as? String
                    ),
                    source = SingletonDefinition.Source(
                        clazz = clazz,
                        method = method
                    ),
                    dependencies = method.parameters
                        .map { methodParameter ->
                            SingletonDefinition.Key(
                                type = methodParameter.type,
                                name = methodParameter.annotations.find { it.isTypeOf(Named::class) }?.arguments?.find { it.name == "value" }?.value as? String
                            )
                        }
                )
            }
    }

}