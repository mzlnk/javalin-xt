package io.mzlnk.javalin.di.core.internal.graph

import io.mzlnk.javalin.di.core.internal.definition.SingletonDefinition

internal class DependencyGraph private constructor(
    private val _nodes: Array<SingletonDefinition>,
    private val _edges: Array<Array<Boolean>>
) {

    val nodes: List<SingletonDefinition> = _nodes.toList()
    val edges: List<Pair<SingletonDefinition, SingletonDefinition>> = _edges
        .mapIndexed { i, row -> row.mapIndexedNotNull { j, edge -> if(edge) i to j else null } }
        .flatten()
        .map { (i, j) -> _nodes[i] to _nodes[j] }

    companion object {

        fun create(definitions: List<SingletonDefinition>): DependencyGraph {
            val nodes: Array<SingletonDefinition> = definitions.toTypedArray()
            val nodesIdxsByKey: Map<SingletonDefinition.Key, List<Int>> = nodes
                .mapIndexed { idx, def -> def.key to idx }
                .groupBy({ it.first }, { it.second })

            val edges: Array<Array<Boolean>> = Array(nodes.size) { Array(nodes.size) { false } }

            nodes.forEachIndexed { idx, node ->
                val dependentNodesIdxs: List<Int> = node.dependencies
                    .flatMap { dep ->
                        nodesIdxsByKey
                            /*
                                node \ dep | no-name | name                   |
                                no-name    |    1    |  0                     |
                                name       |    1    |  node.name == dep.name |
                             */
                            .filterKeys { key -> key.type == dep.type && dep.name?.let { name -> name == key.name } ?: true }
                            .values
                            .flatten()
                            .also {
                                if(dep is SingletonDefinition.Dependency.Single) {
                                    if(it.isEmpty()) {
                                        throw DependencyGraphCreationException.DependencyNotFound()
                                    }
                                    if(it.size > 1) {
                                        throw DependencyGraphCreationException.MultipleDependenciesFound()
                                    }
                                }
                            }
                    }

                dependentNodesIdxs.forEach { dependentNodeIdx ->
                    edges[dependentNodeIdx][idx] = true
                }
            }

            // validate against circular dependencies
            for(i in nodes.indices) {
                for(j in nodes.indices) {
                    if(i == j) {
                        continue
                    }

                    if(edges[i][j] && edges[j][i]) {
                        throw DependencyGraphCreationException.CircularDependencyFound()
                    }
                }
            }

            return DependencyGraph(
                _nodes = nodes,
                _edges = edges
            )
        }
    }

}

internal abstract class DependencyGraphCreationException(message: String) : RuntimeException(message) {

    class DependencyNotFound : DependencyGraphCreationException("Dependency not found")
    class MultipleDependenciesFound : DependencyGraphCreationException("Multiple dependencies found")
    class CircularDependencyFound : DependencyGraphCreationException("Circular dependency found")

}