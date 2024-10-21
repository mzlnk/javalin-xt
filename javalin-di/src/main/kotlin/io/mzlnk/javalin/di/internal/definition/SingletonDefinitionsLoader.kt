package io.mzlnk.javalin.di.internal.definition

import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.Named
import io.mzlnk.javalin.di.Singleton

internal class SingletonDefinitionsLoader(
    private val classSource: ClassSource = ClasspathClassSource
) {

    fun load(): List<SingletonDefinition> {
        val allClasses = classSource.read()

        return allClasses
            .filter { clazz -> clazz.getAnnotation(Module::class.java) != null }
            .flatMap { clazz -> clazz.declaredMethods.asSequence().map { method -> clazz to method } }
            .filter { (_, method) -> method.getAnnotation(Singleton::class.java) != null }
            .map { (clazz, method) ->
                SingletonDefinition(
                    key = SingletonDefinition.Key(
                        type = method.returnType,
                        name = method.getAnnotation(Named::class.java)?.value
                    ),
                    source = SingletonDefinition.Source(
                        clazz = clazz,
                        method = method
                    ),
                    dependencies = method.parameters
                        .map { methodParameter ->
                            SingletonDefinition.Key(
                                type = methodParameter.type,
                                name = methodParameter.getAnnotation(Named::class.java)?.value
                            )
                        }
                )
            }
    }

}

