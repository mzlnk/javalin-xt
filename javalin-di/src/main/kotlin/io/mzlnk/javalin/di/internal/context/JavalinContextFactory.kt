package io.mzlnk.javalin.di.internal.context

internal class JavalinContextFactory(
    private val source: SingletonDefinitionSource = DefaultSingletonDefinitionSource
) {

    fun create(): JavalinContext {
        val definitions = source.definitions()

        val dependencyGraph = DependencyGraphFactory.create(definitions)

        if (dependencyGraph.hasCycles) {
            throw dependencyCycleFoundException(dependencyGraph.cycles)
        }

        val context = JavalinContext()
        dependencyGraph.topologicalOrder.forEach { definition ->
            context.registerSingleton(
                identifier = definition.identifier,
                instance = definition.instanceProvider.invoke(
                    definition.dependencies.map { dependency ->
                        context.findInstance(dependency)
                    }
                )
            )
        }

        return context
    }

}