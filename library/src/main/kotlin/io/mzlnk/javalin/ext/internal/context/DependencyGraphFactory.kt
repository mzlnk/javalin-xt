package io.mzlnk.javalin.ext.internal.context

import io.mzlnk.javalin.ext.definition.SingletonDefinition
import io.mzlnk.javalin.ext.internal.context.SingletonMatcher.Companion.matcherFor
import io.mzlnk.javalin.ext.internal.utils.graph.Graph
import io.mzlnk.javalin.ext.type.TypeReference
import java.util.*

/**
 * Factory component used to create dependency graph based on provided singleton definitions.
 *
 * The resulting graph consists of:
 * - nodes representing singleton definitions
 * - edges representing dependencies between singleton definitions:
 *   - an edge from node A to node B means that node A depends on node B
 *   - if there are multiple nodes matching the dependency, there will be multiple edges
 *
 * @param definitions list of singleton definitions to be used to create the graph
 *
 * @return directed graph representing dependencies between singleton definitions
 */
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
