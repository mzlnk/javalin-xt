package io.mzlnk.javalin.di.internal.utils.graph

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class GraphTest {

    @Test
    fun `should return graph nodes in topological order for acyclic graph`() {
        /*
         * graph:
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

    @Test
    fun `should throw exception when get topological order for cyclic graph`() {
        /*
         * graph:
         * A -> B
         * ^    |
         * |    v
         * D <- C
         */

        // given:
        val graph = graph(
            nodes = listOf(A, B, C, D),
            edges = listOf(
                A to B,
                B to C,
                C to D,
                D to A
            )
        )

        // when:
        val exception = assertThatThrownBy { graph.topologicalOrder }

        // then:
        exception.isInstanceOf(IllegalStateException::class.java)
        exception.hasMessage("Graph contains a cycle")
    }

    @Test
    fun `should return true when graph has cycle`() {
        /*
         * graph:
         * A -> B
         * ^    |
         * |    v
         * D <- C
         */

        // given:
        val graph = graph(
            nodes = listOf(A, B, C, D),
            edges = listOf(
                A to B,
                B to C,
                C to D,
                D to A
            )
        )

        // expect
        assertThat(graph.hasCycle).isTrue()
    }

    @Test
    fun `should return false when graph has no cycle`() {
        /*
         * graph:
         * A -> B
         * |    |
         * v    v
         * D <- C
         */

        // given:
        val graph = graph(
            nodes = listOf(A, B, C, D),
            edges = listOf(
                A to B,
                B to C,
                C to D,
                A to D
            )
        )

        // expect:
        assertThat(graph.hasCycle).isFalse()
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