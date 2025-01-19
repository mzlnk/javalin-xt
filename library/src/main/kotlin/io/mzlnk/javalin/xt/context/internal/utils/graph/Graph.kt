package io.mzlnk.javalin.xt.context.internal.utils.graph

import java.util.*

/**
 * Represents a directed graph of nodes of type [E].
 *
 * @param E type of the nodes
 */
internal class Graph<E>(
    private val _nodes: Array<E>,
    private val _edges: Array<Array<Boolean>>
) {

    /**
     * List of nodes in the graph.
     */
    val nodes: List<E> = _nodes.toList()

    /**
     * List of edges in the graph. Each edge is represented as a pair of nodes.
     * Pair (A, B) means that there is an edge from node A to node B.
     */
    val edges: List<Pair<E, E>> = _edges
        .mapIndexed { i, row -> row.mapIndexedNotNull { j, edge -> if (edge) i to j else null } }
        .flatten()
        .map { (i, j) -> _nodes[i] to _nodes[j] }

    /**
     * List of nodes in topological order.
     *
     * @throws IllegalStateException if the graph contains a cycle
     */
    val topologicalOrder: List<E> by lazy {
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

        result
    }

    /**
     * List of cycles in the graph.
     */
    val cycles: List<Cycle<E>> by lazy {
        val color = Array(_nodes.size) { Color.WHITE }

        val cycles: MutableList<Cycle<E>> = mutableListOf()

        fun findCycle(node: Int, path: List<Int>) {
            // Mark the node as being visited (in progress)
            color[node] = Color.GRAY

            // Explore all neighbors
            for (i in _nodes.indices) {
                if (_edges[node][i]) {
                    if (color[i] == Color.GRAY) {
                        val cycle = path.subList(path.indexOf(i), path.size).map { _nodes[it] }
                        cycles.add(Cycle(cycle))
                    }

                    if (color[i] == Color.WHITE) {
                        findCycle(i, path + i)
                    }
                }
            }

            color[node] = Color.BLACK
        }

        // Check for cycles starting from each node
        for (i in _nodes.indices) {
            if (color[i] == Color.WHITE) {
                findCycle(i, listOf(i))
            }
        }

        cycles
    }

    /**
     * Returns true if the graph contains cycles.
     */
    val hasCycles: Boolean get() = cycles.isNotEmpty()

    private enum class Color {
        WHITE, GRAY, BLACK
    }

}

