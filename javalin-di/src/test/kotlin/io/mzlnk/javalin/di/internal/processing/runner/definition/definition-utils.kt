package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.internal.processing.Method
import io.mzlnk.javalin.di.internal.processing.Type

@DslMarker
@Target(AnnotationTarget.TYPE)
internal annotation class ScopedDsl

internal fun singletonDefinition(
    type: Type,
    name: String? = null,
    init: (@ScopedDsl SingletonDefinitionBuilder).() -> Unit = {}
): SingletonDefinition =
    SingletonDefinitionBuilder().apply(init).build(SingletonDefinition.Key(type, name))


internal class SingletonDefinitionBuilder {

    private var _source: Method? = Method(
        name = "testMethod",
        returnType = Type(
            packageName = "kotlin",
            name = "Unit"
        )
    )

    private var _dependencies: List<SingletonDefinition.Key> = emptyList()
    private var _conditions: List<Condition> = emptyList()

    fun dependencies(init: (@ScopedDsl DependenciesBuilder).() -> Unit) {
        _dependencies = DependenciesBuilder().apply(init).build()
    }

    fun build(key: SingletonDefinition.Key) = SingletonDefinition(
        key = key,
        source = _source!!,
        dependencies = _dependencies,
        conditions = _conditions
    )

    class DependenciesBuilder {

        private val _dependencies = mutableListOf<SingletonDefinition.Key>()

        fun single(init: (@ScopedDsl DependencyBuilder.Single).() -> Unit) {
            _dependencies.add(DependencyBuilder.Single().apply(init).build())
        }

        fun build() = _dependencies.toList()

    }

    // TODO: add support for iterable dependencies/types
    interface DependencyBuilder {

        class Single {

            var type: Type? = null
            var name: String? = null

            fun build() = SingletonDefinition.Key(
                type = type!!,
                name = name
            )

        }
    }
}