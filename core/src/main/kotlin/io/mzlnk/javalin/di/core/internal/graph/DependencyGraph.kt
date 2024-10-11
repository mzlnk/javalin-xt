package io.mzlnk.javalin.di.core.internal.graph

import io.mzlnk.javalin.di.core.internal.definition.SingletonDefinition
import java.util.Queue
import java.util.ArrayDeque

internal class DependencyGraph(
    private val _nodes: Array<SingletonDefinition>,
    private val _edges: Array<Array<Boolean>>
) {

    val nodes: List<SingletonDefinition> = _nodes.toList()
    val edges: List<Pair<SingletonDefinition, SingletonDefinition>> = _edges
        .mapIndexed { i, row -> row.mapIndexedNotNull { j, edge -> if(edge) i to j else null } }
        .flatten()
        .map { (i, j) -> _nodes[i] to _nodes[j] }

    val topologicalOrder: List<SingletonDefinition> = run {
        val nodesIndegrees = IntArray(_nodes.size) { 0 }

        // calculate indegree:
        for (i in _nodes.indices) {
            for (j in _nodes.indices) {
                if (_edges[i][j]) {
                    nodesIndegrees[j]++
                }
            }
        }

        val result: MutableList<SingletonDefinition> = mutableListOf()

        val nodesWithNoIncomingEdge: Queue<Int> = ArrayDeque()
        for (i in _nodes.indices) {
            if (nodesIndegrees[i] == 0) {
                nodesWithNoIncomingEdge.offer(i)
            }
        }

        while (nodesWithNoIncomingEdge.isNotEmpty()) {
            val node = nodesWithNoIncomingEdge.poll()
            result.add(_nodes[node])

            for (i in _nodes.indices) {
                if (_edges[node][i]) {
                    nodesIndegrees[i]--

                    if (nodesIndegrees[i] == 0) {
                        nodesWithNoIncomingEdge.add(i)
                    }
                }
            }
        }

        if (result.size != _nodes.size) {
            throw DependencyGraphCreationException.CircularDependencyFound()
        }

        result
    }

}

