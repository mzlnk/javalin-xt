package io.mzlnk.javalin.di.internal.context

internal class JavalinContextFactory(
    private val source: SingletonDefinitionSource = DefaultSingletonDefinitionSource
) {

    fun create(): JavalinContext {
        val definitions = source.definitions()
        val dependencyGraph = DependencyGraphFactory.create(definitions)

        val context = JavalinContext()
        dependencyGraph.topologicalOrder.forEach { definition ->
            context.registerSingleton(
                type = definition.type,
                instance = definition.instanceProvider.invoke(
                    definition.dependencies.map { context.findSingleton(it) }
                )
            )
        }

        return context
    }
}