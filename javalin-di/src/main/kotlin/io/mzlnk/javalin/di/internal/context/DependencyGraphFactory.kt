package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.context.SingletonMatcher.Companion.matcherFor
import io.mzlnk.javalin.di.internal.utils.graph.Graph
import io.mzlnk.javalin.di.type.TypeReference
import java.util.*

internal object DependencyGraphFactory {

    @Suppress("UNCHECKED_CAST")
    fun create(definitions: List<SingletonDefinition<*>>): Graph<SingletonDefinition<*>> {
        val nodes: Array<SingletonDefinition<*>> = definitions.toTypedArray()
        val edges: Array<Array<Boolean>> = Array(nodes.size) { Array(nodes.size) { false } }

        val nodeIdxsByIds: Map<UUID, Int> = nodes
            .mapIndexed { idx, definition -> definition.id to idx }
            .associate { it }

        nodes.forEach { node ->
            node.dependencies.forEach { dependency ->
                val matcher = if(dependency.typeRef.isList()) {
                    matcherFor(SingletonDefinition.Identifier((dependency.typeRef as TypeReference<List<Any>>).elementType))
                } else {
                    matcherFor(dependency)
                }

                nodes
                    // skip the node itself
                    .filter { candidate -> candidate.id != node.id }
                    // apply filters:
                    .filter { candidate -> matcher.matches(candidate.identifier) }
                    // set corresponding edges
                    .forEach { candidate ->
                        edges[nodeIdxsByIds[candidate.id]!!][nodeIdxsByIds[node.id]!!] = true
                    }

            }
        }

        return Graph(
            _nodes = nodes,
            _edges = edges
        )
    }


}
