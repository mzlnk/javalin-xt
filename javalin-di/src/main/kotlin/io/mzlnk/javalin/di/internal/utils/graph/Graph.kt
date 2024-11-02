package io.mzlnk.javalin.di.internal.utils.graph

import java.util.*

internal class Graph<E>(
    private val _nodes: Array<E>,
    private val _edges: Array<Array<Boolean>>
) {

    val nodes: List<E> = _nodes.toList()
    val edges: List<Pair<E, E>> = _edges
        .mapIndexed { i, row -> row.mapIndexedNotNull { j, edge -> if (edge) i to j else null } }
        .flatten()
        .map { (i, j) -> _nodes[i] to _nodes[j] }

    val topologicalOrder: List<E> = run {
        val nodesIndegrees = IntArray(_nodes.size) { 0 }

        // calculate indegree:
        for (i in _nodes.indices) {
            for (j in _nodes.indices) {
                if (_edges[i][j]) {
                    nodesIndegrees[j]++
                }
            }
        }

        val result: MutableList<E> = mutableListOf()

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
            throw IllegalStateException("Graph contains a cycle")
        }

        result
    }

}

