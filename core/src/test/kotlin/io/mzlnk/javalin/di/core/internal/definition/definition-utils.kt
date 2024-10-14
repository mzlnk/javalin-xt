package io.mzlnk.javalin.di.core.internal.definition

import java.lang.reflect.Method

@DslMarker
@Target(AnnotationTarget.TYPE)
internal annotation class ScopedDsl

internal fun singletonDefinition(
    type: Class<*>,
    name: String? = null,
    init: (@ScopedDsl SingletonDefinitionBuilder).() -> Unit = {}
): SingletonDefinition =
    SingletonDefinitionBuilder().apply(init).build(SingletonDefinition.Key(type, name))


internal class SingletonDefinitionBuilder {

    private var _source: SingletonDefinition.Source? = SingletonDefinition.Source(
        clazz = TestModule::class.java,
        method = TestModule::class.java.getDeclaredMethod("testMethod")

    )

    private var _dependencies: List<SingletonDefinition.Key> = emptyList()
    private var _conditions: List<SingletonDefinition.Condition> = emptyList()

    fun source(init: (@ScopedDsl SourceBuilder).() -> Unit) {
        _source = SourceBuilder().apply(init).build()
    }

    fun dependencies(init: (@ScopedDsl DependenciesBuilder).() -> Unit) {
        _dependencies = DependenciesBuilder().apply(init).build()
    }

    fun build(key: SingletonDefinition.Key) = SingletonDefinition(
        key = key,
        source = _source!!,
        dependencies = _dependencies,
        conditions = _conditions
    )

    class SourceBuilder {

        var clazz: Class<*>? = null
        var method: Method? = null

        fun build() = SingletonDefinition.Source(
            clazz = clazz!!,
            method = method!!
        )

    }

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

            var type: Class<*>? = null
            var name: String? = null

            fun build() = SingletonDefinition.Key(
                type = type!!,
                name = name
            )

        }
    }

    private class TestModule {

        fun testMethod() {}

    }

}