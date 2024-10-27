package io.mzlnk.javalin.di.internal.processing.runner.graph

import io.mzlnk.javalin.di.internal.processing.runner.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.processing.runner.graph.DependencyGraph

internal fun dependencyGraph(
    nodes: List<SingletonDefinition>,
    edges: List<Pair<SingletonDefinition, SingletonDefinition>>
): DependencyGraph {
    val _nodes = nodes.toTypedArray()
    val _edges = Array(nodes.size) { Array(nodes.size) { false } }

    edges.forEach { (from, to) ->
        val fromIdx = nodes.indexOf(from)
        val toIdx = nodes.indexOf(to)

        _edges[fromIdx][toIdx] = true
    }

    return DependencyGraph(_nodes, _edges)
}