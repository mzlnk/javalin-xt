package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.utils.graph.Cycle
import io.mzlnk.javalin.di.type.TypeReference

internal class JavalinContextFactory(
    private val source: SingletonDefinitionSource = DefaultSingletonDefinitionSource
) {

    fun create(): JavalinContext {
        val definitions = source.definitions()

        val dependencyGraph = DependencyGraphFactory.create(definitions)

        if (dependencyGraph.hasCycles) {
            throw DependencyCycleFoundException(dependencyGraph.cycles)
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

private class IterableSingletonDefinitionNotSupported(identifier: SingletonDefinition.Identifier<*>) :
    JavalinContextException() {

    override val message = "Iterable singleton definition `$identifier` is not supported."

}