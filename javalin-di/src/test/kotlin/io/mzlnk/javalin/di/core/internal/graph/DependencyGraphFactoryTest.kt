package io.mzlnk.javalin.di.core.internal.graph

import io.mzlnk.javalin.di.core.internal.definition.singletonDefinition
import io.mzlnk.javalin.di.core.internal.utils.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class DependencyGraphFactoryTest {

    @Test
    fun `should create graph for dependent singletons`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single { type = B }
            }
        }
        val singletonB = singletonDefinition(B)

        // and:
        val definitions = listOf(singletonA, singletonB)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactly(singletonB to singletonA)
    }

    @Test
    fun `should create graph for chain of dependent singletons`() {
        /*
         * dependency graph:
         * A <- B <- C
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single { type = B }
            }
        }
        val singletonB = singletonDefinition(B) {
            dependencies {
                single { type = C }
            }
        }
        val singletonC = singletonDefinition(C)

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactlyInAnyOrder(
            singletonC to singletonB,
            singletonB to singletonA
        )
    }

    @Test
    fun `should create graph for multiple dependency trees`() {
        /*
         * dependency graph:
         * A <- B
         * C <- D
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single { type = B }
            }
        }

        val singletonB = singletonDefinition(B)

        val singletonC = singletonDefinition(C) {
            dependencies {
                single { type = D }
            }
        }

        val singletonD = singletonDefinition(D)

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC, singletonD)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactlyInAnyOrder(
            singletonB to singletonA,
            singletonD to singletonC
        )
    }

    @Test
    fun `should create graph for multiple dependencies for one singleton`() {
        /*
         * dependency graph:
         * C -> A <- B
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single { type = B }
                single { type = C }
            }
        }

        val singletonB = singletonDefinition(B)
        val singletonC = singletonDefinition(C)

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactlyInAnyOrder(
            singletonB to singletonA,
            singletonC to singletonA
        )
    }

    @Test
    fun `should create graph for multiple dependencies for multiple singletons in chain`() {
        /*
         * dependency graph:
         * C -> A
         *      ^
         *      |
         * D -> B <- E
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single { type = C }
                single { type = B }
            }
        }

        val singletonB = singletonDefinition(B) {
            dependencies {
                single { type = D }
                single { type = E }
            }
        }

        val singletonC = singletonDefinition(C)
        val singletonD = singletonDefinition(D)
        val singletonE = singletonDefinition(E)

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC, singletonD, singletonE)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactlyInAnyOrder(
            singletonC to singletonA,
            singletonB to singletonA,
            singletonD to singletonB,
            singletonE to singletonB
        )
    }

    @Test
    fun `should create graph for singleton dependent to multiple singletons`() {
        /*
         * dependency graph:
         * A <- C -> B
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single { type = C }
            }
        }

        val singletonB = singletonDefinition(B) {
            dependencies {
                single { type = C }
            }
        }

        val singletonC = singletonDefinition(C)

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactlyInAnyOrder(
            singletonC to singletonA,
            singletonC to singletonB
        )
    }

    @Test
    fun `should create graph for diamond shaped dependencies`() {
        /*
         * dependency graph:
         *   -> B ->
         * D         A
         *   -> C ->
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single { type = B }
                single { type = C }
            }
        }

        val singletonB = singletonDefinition(B) {
            dependencies {
                single { type = D }
            }
        }

        val singletonC = singletonDefinition(C) {
            dependencies {
                single { type = D }
            }
        }

        val singletonD = singletonDefinition(D)

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC, singletonD)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactlyInAnyOrder(
            singletonB to singletonA,
            singletonC to singletonA,
            singletonD to singletonB,
            singletonD to singletonC
        )
    }

    @Test
    fun `should create graph for named dependency`() {
        /*
         * dependency graph:
         * A <- B<b1>
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single {
                    type = B
                    name = "b1"
                }
            }
        }
        val singletonB_b1 = singletonDefinition(B, name = "b1")

        // and:
        val definitions = listOf(singletonA, singletonB_b1)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactly(singletonB_b1 to singletonA)
    }

    @Test
    fun `should create graph for multiple named dependencies`() {
        /*
         * dependency graph:
         * A <- C<c1>
         * B <- C<c2>
         * C
         */

        // given:
        val singletonA = singletonDefinition(A) {
            dependencies {
                single {
                    type = C
                    name = "c1"
                }
            }
        }
        val singletonB = singletonDefinition(B) {
            dependencies {
                single {
                    type = C
                    name = "c2"
                }
            }
        }

        val singletonC_c1 = singletonDefinition(C, name = "c1")
        val singletonC_c2 = singletonDefinition(C, name = "c2")
        val singletonC = singletonDefinition(C)

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC_c1, singletonC_c2, singletonC)

        // when:
        val graph = DependencyGraphFactory.create(definitions)

        // then:
        assertThat(graph.nodes).isEqualTo(definitions)
        assertThat(graph.edges).containsExactlyInAnyOrder(
            singletonC_c1 to singletonA,
            singletonC_c2 to singletonB
        )
    }

    @Test
    @Disabled("Not implemented yet")
    fun `should create graph for iterable dependency with one dependency`() {
        /*
         * dependency graph:
         * A <- B
         * dep(A) = List<B>
         */
    }

    @Test
    @Disabled("Not implemented yet")
    fun `should create graph for iterable dependency with no dependencies`() {
        /*
         * dependency graph:
         * A
         * dep(A) = List<B>
         */
    }

    @Test
    @Disabled("Not implemented yet")
    fun `should create graph for iterable dependency with multiple dependencies`() {
        /*
         * dependency graph:
         * A <- B
         * dep(A) = List<B>
         */
    }

    @Test
    @Disabled("Not implemented yet")
    fun `should create graph for iterable dependency with one named dependency`() {
        /*
         * dependency graph:
         * A <- B
         * dep(A) = List<B>
         */
    }

    @Test
    @Disabled("Not implemented yet")
    fun `should create graph for iterable named dependency with one named dependency`() {
        /*
         * dependency graph:
         * A <- B<b1>
         * dep(A) = List<B<b1>>
         */
    }

    @Test
    @Disabled("Not implemented yet")
    fun `should create graph for iterable named dependency with multiple named dependencies`() {
        /*
         * dependency graph:
         * A <- B<b1>
         * dep(A) = List<B<b1>>
         */
    }

}