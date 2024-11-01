package io.mzlnk.javalin.di.internal.context

@DslMarker
@Target(AnnotationTarget.TYPE)
internal annotation class ScopedDsl

internal fun singletonDefinition(
    type: Class<*>,
    init: (@ScopedDsl SingletonDefinitionBuilder).() -> Unit = {}
): SingletonDefinition<*> =
    SingletonDefinitionBuilder().apply(init).build(type)


internal class SingletonDefinitionBuilder {

    private var _dependencies: List<Class<*>> = emptyList()

    fun dependencies(init: (@ScopedDsl DependenciesBuilder).() -> Unit) {
        _dependencies = DependenciesBuilder().apply(init).build()
    }

    fun <T : Any> build(type: Class<T>) = SingletonDefinition(
        type = type,
        dependencies = _dependencies,
        instanceProvider = { TODO() }
    )

    class DependenciesBuilder {

        private val _dependencies = mutableListOf<Class<*>>()

        fun single(type: Class<*>) {
            _dependencies.add(type)
        }

        fun build() = _dependencies.toList()

    }
}