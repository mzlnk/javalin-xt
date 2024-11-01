package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DependencyGraphTest {

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
        val singletonA = singletonDefinition(A)
        val singletonB = singletonDefinition(B)
        val singletonC = singletonDefinition(C)
        val singletonD = singletonDefinition(D)
        val singletonE = singletonDefinition(E)

        val definitions = listOf(singletonA, singletonB, singletonC, singletonD, singletonE)

        // and:
        val graph = dependencyGraph(
            nodes = definitions,
            edges = listOf(
                singletonC to singletonA,
                singletonD to singletonB,
                singletonB to singletonA,
                singletonE to singletonB
            )
        )

        // when:
        val topologicalOrder = graph.topologicalOrder

        // then:
        assertThat(topologicalOrder).containsExactly(
            singletonC,
            singletonD,
            singletonE,
            singletonB,
            singletonA
        )
    }

    private companion object {

        private class TypeA
        private class TypeB
        private class TypeC
        private class TypeD
        private class TypeE

        private val A: Class<*> = TypeA::class.java
        private val B: Class<*> = TypeB::class.java
        private val C: Class<*> = TypeC::class.java
        private val D: Class<*> = TypeD::class.java
        private val E: Class<*> = TypeE::class.java

        private fun singletonDefinition(type: Class<*>): SingletonDefinition<*> {
            return SingletonDefinition(
                type = type,
                dependencies = emptyList(),
                instanceProvider = { TODO() }
            )
        }

        private fun dependencyGraph(
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

    }

}