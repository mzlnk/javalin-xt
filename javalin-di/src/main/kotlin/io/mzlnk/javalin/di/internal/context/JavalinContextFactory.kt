package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.utils.graph.Cycle

internal class JavalinContextFactory(
    private val source: SingletonDefinitionSource = DefaultSingletonDefinitionSource
) {

    fun create(): JavalinContext {
        val definitions = source.definitions()

        definitions
            .find { it.identifier is SingletonDefinition.Identifier.Iterable<*> }
            ?.let { throw IterableSingletonDefinitionNotSupported(it.identifier as SingletonDefinition.Identifier.Iterable<*>) }

        val dependencyGraph = DependencyGraphFactory.create(definitions)

        if (dependencyGraph.hasCycles) {
            throw DependencyCycleFoundException(dependencyGraph.cycles)
        }

        val context = JavalinContext()
        dependencyGraph.topologicalOrder.forEach { definition ->
            context.registerSingleton(
                definition.instanceProvider.invoke(
                    definition.dependencies.map { dependency ->
                        when (dependency) {
                            is SingletonDefinition.Identifier.Single<*> -> context.getSingleton(dependency)
                            is SingletonDefinition.Identifier.Iterable<*> -> context.getSingletonList(dependency)
                        }
                    }
                )
            )
        }

        return context
    }

}

private class DependencyCycleFoundException(cycles: List<Cycle<SingletonDefinition<*>>>) : JavalinContextException() {

    override val message = StringBuilder()
        .append("Failed to create context due to dependency cycle(s):")
        .apply {
            cycles.forEachIndexed { idx, cycle ->
                append("\nCycle #${idx + 1}:\n")
                append(cycle.toString())
                if (idx < cycles.size - 1) append("\n")
            }
        }
        .toString()

}

private class IterableSingletonDefinitionNotSupported(identifier: SingletonDefinition.Identifier.Iterable<*>) :
    JavalinContextException() {

    override val message = "Iterable singleton definition `$identifier` is not supported."

}