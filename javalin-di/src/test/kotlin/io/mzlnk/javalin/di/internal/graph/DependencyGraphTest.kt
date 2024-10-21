package io.mzlnk.javalin.di.internal.graph

import io.mzlnk.javalin.di.internal.definition.singletonDefinition
import io.mzlnk.javalin.di.internal.utils.*
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

}