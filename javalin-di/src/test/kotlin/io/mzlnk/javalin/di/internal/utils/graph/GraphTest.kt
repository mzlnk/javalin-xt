package io.mzlnk.javalin.di.internal.utils.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GraphTest {

    @Test
    fun `should return graph nodes in topological order`() {
        /*
         * dependency graph:
         * C -> A
         *      ^
         *      |
         * D -> B <- E
         */

        // given:
        val graph = graph(
            nodes = listOf(A, B, C, D, E),
            edges = listOf(
                C to A,
                D to B,
                B to A,
                E to B
            )
        )

        // when:
        val topologicalOrder = graph.topologicalOrder

        // then:
        assertThat(topologicalOrder).containsExactly(C, D, E, B, A)
    }

    private companion object {

        private data class Element(val id: String)

        private val A: Element = Element(id = "A")
        private val B: Element = Element(id = "B")
        private val C: Element = Element(id = "C")
        private val D: Element = Element(id = "D")
        private val E: Element = Element(id = "E")

        private fun graph(
            nodes: List<Element>,
            edges: List<Pair<Element, Element>>
        ): Graph<Element> {
            val _nodes = nodes.toTypedArray()
            val _edges = Array(nodes.size) { Array(nodes.size) { false } }

            edges.forEach { (from, to) ->
                val fromIdx = nodes.indexOf(from)
                val toIdx = nodes.indexOf(to)

                _edges[fromIdx][toIdx] = true
            }

            return Graph(_nodes, _edges)
        }

    }

}