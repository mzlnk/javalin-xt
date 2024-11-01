package io.mzlnk.javalin.di.internal.context

internal fun dependencyGraph(
    nodes: List<SingletonDefinition<*>>,
    edges: List<Pair<SingletonDefinition<*>, SingletonDefinition<*>>>
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