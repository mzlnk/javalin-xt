package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinitionProvider
import java.util.*

internal object JavalinContextFactory {

    fun create(): JavalinContext {
        val providers = ServiceLoader.load(SingletonDefinitionProvider::class.java).toList()

        val definitions = providers.flatMap { it.definitions }
        val dependencyGraph = DependencyGraphFactory.create(definitions)

        val context = JavalinContext()
        dependencyGraph.topologicalOrder.forEach { definition ->
            context.registerSingleton(
                type = definition.type,
                instance = definition.instanceProvider.invoke(
                    definition.dependencies.map { context.getSingleton(it) }
                )
            )
        }

        return context
    }
}