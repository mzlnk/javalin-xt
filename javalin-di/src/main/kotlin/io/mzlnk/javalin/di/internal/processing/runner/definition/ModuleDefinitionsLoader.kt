package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.Named
import io.mzlnk.javalin.di.Singleton
import io.mzlnk.javalin.di.internal.processing.Annotation
import io.mzlnk.javalin.di.internal.processing.Project
import kotlin.reflect.KClass

internal object ModuleDefinitionsLoader {

    fun load(project: Project): List<ModuleDefinition> {
        val allClasses = project.classes

        return allClasses
            .filter { clazz -> clazz.annotations.any { it.isTypeOf(Module::class) } }
            .map { clazz ->
                val singletons = clazz.methods
                    .filter { method -> method.annotations.any { it.isTypeOf(Singleton::class) } }
                    .map { method ->
                        SingletonDefinition(
                            key = SingletonDefinition.Key(
                                type = method.returnType,
                                name = method.annotations.find { it.isTypeOf(Named::class) }?.arguments?.find { it.name == "value" }?.value as? String
                            ),
                            source = method,
                            dependencies = method.parameters
                                .map { methodParameter ->
                                    SingletonDefinition.Key(
                                        type = methodParameter.type,
                                        name = methodParameter.annotations.find { it.isTypeOf(Named::class) }?.arguments?.find { it.name == "value" }?.value as? String
                                    )
                                },
                            conditions = emptyList() // TODO: implement condition support
                        )
                    }

                ModuleDefinition(
                    source = clazz,
                    singletons = singletons,
                    conditions = emptyList() // TODO: implement condition support
                )
            }

    }

}

private fun Annotation.isTypeOf(clazz: KClass<*>): Boolean {
    return clazz.qualifiedName == type.qualifiedName
}