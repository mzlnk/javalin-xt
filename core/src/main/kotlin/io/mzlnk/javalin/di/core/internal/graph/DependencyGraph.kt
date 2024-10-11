package io.mzlnk.javalin.di.core.internal.graph

import io.mzlnk.javalin.di.core.internal.definition.SingletonDefinition

internal class DependencyGraph(
    private val _nodes: Array<SingletonDefinition>,
    private val _edges: Array<Array<Boolean>>
) {

    val nodes: List<SingletonDefinition> = _nodes.toList()
    val edges: List<Pair<SingletonDefinition, SingletonDefinition>> = _edges
        .mapIndexed { i, row -> row.mapIndexedNotNull { j, edge -> if(edge) i to j else null } }
        .flatten()
        .map { (i, j) -> _nodes[i] to _nodes[j] }

}

