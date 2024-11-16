package io.mzlnk.javalin.ext.internal.utils.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CycleTest {

    @Test
    fun `should return graphical representation of a cycle`() {
        // given:
        val cycle = Cycle(listOf(A, B, C, D))

        // expect:
        assertThat(cycle.toString()).isEqualTo(
            """
            ┌->A -> B -> C -> D -┐
            └--------------------┘
            """.trimIndent()
        )
    }

    private companion object {

        private data class Element(val id: String) {
            override fun toString(): String = id
        }

        private val A = Element("A")
        private val B = Element("B")
        private val C = Element("C")
        private val D = Element("D")

    }

}