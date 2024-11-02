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

    val topologicalOrder: List<E> get() {
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

        if(result.size != _nodes.size) {
            throw IllegalStateException("Graph contains a cycle")
        }

        return result
    }

    val hasCycle: Boolean get() {
        val color = Array(_nodes.size) { Color.WHITE }

        fun hasCycle(node: Int): Boolean {
            // Mark the node as being visited (in progress)
            color[node] = Color.GRAY

            // Explore all neighbors
            for (i in _nodes.indices) {
                if (_edges[node][i]) {
                    if (color[i] == Color.GRAY) {
                        return true
                    }

                    if (color[i] == Color.WHITE && hasCycle(i)) {
                        return true
                    }
                }
            }

            color[node] = Color.BLACK
            return false
        }

        // Check for cycles starting from each node
        for (i in _nodes.indices) {
            if (color[i] == Color.WHITE && hasCycle(i)) {
                return true
            }
        }

        return false
    }

    private enum class Color {
        WHITE, GRAY, BLACK
    }

}

