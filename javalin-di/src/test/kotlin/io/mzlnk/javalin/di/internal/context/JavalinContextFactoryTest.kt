@file:Suppress("UNCHECKED_CAST")

package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.type.TypeReference
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

class JavalinContextFactoryTest {

    @Test
    fun `should create context for dependent components`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getOne(identifier(ComponentB::class.java)) ?: fail("Component B not found")

        assertThat(componentA.components["B"]).isEqualTo(componentB)
    }

    @Test
    fun `should create context for chain of dependent components`() {
        /*
         * dependency graph:
         * A <- B <- C
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentC::class.java)),
            instanceProvider = { ComponentB("C" to it[0] as ComponentC) }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentC() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getOne(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getOne(identifier(ComponentC::class.java)) ?: fail("Component C not found")

        assertThat(componentA.components["B"]).isEqualTo(componentB)
        assertThat(componentB.components["C"]).isEqualTo(componentC)
    }

    @Test
    fun `should create context for multiple component dependency trees`() {
        /*
         * dependency graph:
         * A <- B
         * C <- D
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(identifier(ComponentD::class.java)),
            instanceProvider = { ComponentC("D" to it[0] as ComponentD) }
        )

        val singletonD = SingletonDefinition(
            identifier = identifier(ComponentD::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentD() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC, singletonD)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(4)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getOne(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getOne(identifier(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.getOne(identifier(ComponentD::class.java)) ?: fail("Component D not found")

        assertThat(componentA.components["B"]).isEqualTo(componentB)
        assertThat(componentC.components["D"]).isEqualTo(componentD)
    }

    @Test
    fun `should create context for component having multiple dependencies`() {
        /*
         * dependency graph:
         * C -> A <- B
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java), identifier(ComponentC::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB, "C" to it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentC() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getOne(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getOne(identifier(ComponentC::class.java)) ?: fail("Component C not found")

        assertThat(componentA.components["B"]).isEqualTo(componentB)
        assertThat(componentA.components["C"]).isEqualTo(componentC)
    }

    @Test
    fun `should create context for component being dependency for multiple components`() {
        /*
         * dependency graph:
         * C <- A -> B
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentA::class.java)),
            instanceProvider = { ComponentB("A" to it[0] as ComponentA) }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(identifier(ComponentA::class.java)),
            instanceProvider = { ComponentC("A" to it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getOne(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getOne(identifier(ComponentC::class.java)) ?: fail("Component C not found")

        assertThat(componentB.components["A"]).isEqualTo(componentA)
        assertThat(componentC.components["A"]).isEqualTo(componentA)
    }

    @Test
    fun `should create context for multiple components having multiple dependencies in chain`() {
        /*
         * dependency graph:
         * C -> A
         *      ^
         *      |
         * D -> B <- E
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java), identifier(ComponentC::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB, "C" to it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentD::class.java), identifier(ComponentE::class.java)),
            instanceProvider = { ComponentB("D" to it[0] as ComponentD, "E" to it[1] as ComponentE) }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentC() }
        )

        val singletonD = SingletonDefinition(
            identifier = identifier(ComponentD::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentD() }
        )

        val singletonE = SingletonDefinition(
            identifier = identifier(ComponentE::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentE() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC, singletonD, singletonE)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(5)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getOne(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getOne(identifier(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.getOne(identifier(ComponentD::class.java)) ?: fail("Component D not found")
        val componentE = context.getOne(identifier(ComponentE::class.java)) ?: fail("Component E not found")

        assertThat(componentA.components["B"]).isEqualTo(componentB)
        assertThat(componentA.components["C"]).isEqualTo(componentC)
        assertThat(componentB.components["D"]).isEqualTo(componentD)
        assertThat(componentB.components["E"]).isEqualTo(componentE)
    }

    @Test
    fun `should create context for diamond shaped component dependency tree`() {
        /*
         * dependency graph:
         *   -> B ->
         * D         A
         *   -> C ->
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java), identifier(ComponentC::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB, "C" to it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentD::class.java)),
            instanceProvider = { ComponentB("D" to it[0] as ComponentD) }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(identifier(ComponentD::class.java)),
            instanceProvider = { ComponentC("D" to it[0] as ComponentD) }
        )

        val singletonD = SingletonDefinition(
            identifier = identifier(ComponentD::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentD() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC, singletonD)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(4)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getOne(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getOne(identifier(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.getOne(identifier(ComponentD::class.java)) ?: fail("Component D not found")

        assertThat(componentA.components["B"]).isEqualTo(componentB)
        assertThat(componentA.components["C"]).isEqualTo(componentC)
        assertThat(componentB.components["D"]).isEqualTo(componentD)
        assertThat(componentC.components["D"]).isEqualTo(componentD)
    }

    @Test
    fun `should create context for component having dependency which is supertype`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = identifier(ComponentB1::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB1() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB1)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java))
        val componentB1 = context.getOne(identifier(ComponentB1::class.java))

        assertThat(componentA.components["B"]).isEqualTo(componentB1)
    }

    @Test
    fun `should create context for component having dependency which interface type`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(TypeB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as TypeB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        // and:
        val definitions = listOf(singletonA, singletonB)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java))
        val componentB = context.getOne(identifier(ComponentB::class.java))

        assertThat(componentA.components["B"]).isEqualTo(componentB)
    }

    @Test
    fun `should create context for component having iterable dependency of given type`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val componentB1 = ComponentB()
        val componentB2 = ComponentB()

        // and:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(object : TypeReference<List<ComponentB>>() {})),
            instanceProvider = { ComponentA("Bs" to it[0] as List<ComponentB>) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB1 }
        )

        val singletonB2 = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB2 }
        )

        // and:
        val definitions = listOf(singletonA, singletonB1, singletonB2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.getOne(identifier(ComponentA::class.java))
        assertThat(componentA.components["Bs"] as List<ComponentB>).containsExactlyInAnyOrder(componentB1, componentB2)
    }

    @Test
    fun `should create context for component having iterable dependency of supertype`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val componentB1 = ComponentB1()
        val componentB2 = ComponentB2()

        // and:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(object : TypeReference<List<ComponentB>>() {})),
            instanceProvider = { ComponentA("Bs" to it[0] as List<ComponentB>) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = identifier(ComponentB1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB1 }
        )

        val singletonB2 = SingletonDefinition(
            identifier = identifier(ComponentB2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB2 }
        )

        // and:
        val definitions = listOf(singletonA, singletonB1, singletonB2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA = context.getOne(identifier(ComponentA::class.java))
        assertThat(componentA.components["Bs"] as List<ComponentB>).containsExactlyInAnyOrder(componentB1, componentB2)
    }

    @Test
    fun `should create context for component having iterable dependency of interface type`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val componentB1 = ComponentB1()
        val componentB2 = ComponentB2()

        // and:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(object : TypeReference<List<TypeB>>() {})),
            instanceProvider = { ComponentA("Bs" to it[0] as List<TypeB>) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = identifier(ComponentB1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB1 }
        )

        val singletonB2 = SingletonDefinition(
            identifier = identifier(ComponentB2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB2 }
        )

        // and:
        val definitions = listOf(singletonA, singletonB1, singletonB2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA = context.getOne(identifier(ComponentA::class.java))
        assertThat(componentA.components["Bs"] as List<ComponentB>).containsExactlyInAnyOrder(componentB1, componentB2)
    }

    @Test
    fun `should create context component having iterable dependency of type but no candidates`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(object : TypeReference<List<ComponentB>>() {})),
            instanceProvider = { ComponentA("Bs" to it[0] as List<ComponentB>) }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA = context.getOne(identifier(ComponentA::class.java))
        assertThat(componentA.components["Bs"] as List<ComponentB>).isEmpty()
    }

    @Test
    fun `should access singleton by its type`() {
        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThatCode { context.getOne(identifier(ComponentA::class.java)) }.doesNotThrowAnyException()
    }

    @Test
    fun `should access the same singleton by its supertype`() {
        // given:
        val singletonB1 = SingletonDefinition(
            identifier = identifier(ComponentA1::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA1() }
        )

        // and:
        val definitions = listOf(singletonB1)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA1 = context.getOne(identifier(ComponentA1::class.java))
        val componentA = context.getOne(identifier(ComponentA::class.java))

        assertThat(componentA1).isEqualTo(componentA)
    }

    @Test
    fun `should access the same singleton by implemented type`() {
        // given:
        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        // and:
        val definitions = listOf(singletonB)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentB = context.getOne(identifier(ComponentB::class.java))
        val typeB = context.getOne(identifier(TypeB::class.java))

        assertThat(componentB).isEqualTo(typeB)
    }

    @Test
    fun `should access all singletons by type`() {
        // given:
        val componentA1 = ComponentA()
        val componentA2 = ComponentA()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val components = context.findAll(identifier(ComponentA::class.java))
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should access all singletons by supertype`() {
        // given:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val components = context.findAll(identifier(ComponentA::class.java))
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should access all singletons by implemented interface`() {
        // given:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val components = context.findAll(identifier(TypeA::class.java))
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should access no singletons if there are no candidates for given type`() {
        // given:
        // no definitions for ComponentA

        // and:
        val definitions = emptyList<SingletonDefinition<*>>()

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val components = context.findAll(identifier(ComponentA::class.java))
        assertThat(components).isEmpty()
    }

    @Test
    fun `should throw exception if there are multiple candidates for given component dependency`() {
        // given:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentA::class.java)),
            instanceProvider = { ComponentB("A" to it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2, singletonB)

        // when:
        val exception = assertThatThrownBy {
            JavalinContextFactory(source = { definitions }).create()
        }

        // then:
        exception.isInstanceOf(JavalinContextException::class.java)
        exception.hasMessage("Multiple candidates found for io.mzlnk.javalin.di.internal.context.ComponentA")
    }

    @Test
    fun `should throw exception if there are no candidates for given component dependency`() {
        // given:
        // no definition for ComponentA

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentA::class.java)),
            instanceProvider = { ComponentB("A" to it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonB)

        // when:
        val exception = assertThatThrownBy {
            JavalinContextFactory(source = { definitions }).create()
        }

        // then:
        exception.isInstanceOf(JavalinContextException::class.java)
        exception.hasMessage("No candidates found for io.mzlnk.javalin.di.internal.context.ComponentA")
    }

    @Test
    fun `should throw exception if there is a dependency cycle between components`() {
        /*
         * dependency graph:
         * ┌->A -> B -> C -┐
         * └---------------┘
         */

        // given:
        val definitions = listOf(
            SingletonDefinition(
                identifier = identifier(ComponentA::class.java),
                dependencies = listOf(identifier(ComponentB::class.java)),
                instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
            ),
            SingletonDefinition(
                identifier = identifier(ComponentB::class.java),
                dependencies = listOf(identifier(ComponentC::class.java)),
                instanceProvider = { ComponentB("C" to it[0] as ComponentC) }
            ),
            SingletonDefinition(
                identifier = identifier(ComponentC::class.java),
                dependencies = listOf(identifier(ComponentA::class.java)),
                instanceProvider = { ComponentC("A" to it[0] as ComponentA) }
            )
        )

        // when:
        val exception = assertThatThrownBy {
            JavalinContextFactory(source = { definitions }).create()
        }

        // then:
        exception.isInstanceOf(JavalinContextException::class.java)
        exception.hasMessage(
            """
            Failed to create context due to dependency cycle(s):
            Cycle #1:
            ┌->io.mzlnk.javalin.di.internal.context.ComponentA -> io.mzlnk.javalin.di.internal.context.ComponentC -> io.mzlnk.javalin.di.internal.context.ComponentB -┐
            └---------------------------------------------------------------------------------------------------------------------------------------------------------┘
            """.trimIndent()
        )
    }

    private companion object {

        inline fun <reified T : Any> identifier(type: Class<T>): SingletonDefinition.Identifier<T> =
            SingletonDefinition.Identifier(typeRef = object : TypeReference<T>() {})

        inline fun <reified T : Any> identifier(typeRef: TypeReference<T>): SingletonDefinition.Identifier<T> =
            SingletonDefinition.Identifier(typeRef = typeRef)
    }

}

// general purpose types for testing
private interface TypeA
private interface TypeB

// general purpose classes for testing
private open class ComponentA(vararg components: Pair<String, Any>) : TypeA { val components = mapOf(*components) }
private open class ComponentB(vararg components: Pair<String, Any>) : TypeB { val components = mapOf(*components) }
private open class ComponentC(vararg components: Pair<String, Any>) { val components = mapOf(*components) }
private open class ComponentD(vararg components: Pair<String, Any>) { val components = mapOf(*components) }
private open class ComponentE(vararg components: Pair<String, Any>) { val components = mapOf(*components) }

private class ComponentA1(vararg components: Pair<String, Any>) : ComponentA(*components)
private class ComponentA2(vararg components: Pair<String, Any>) : ComponentA(*components)

private class ComponentB1(vararg components: Pair<String, Any>) : ComponentB(*components)
private class ComponentB2(vararg components: Pair<String, Any>) : ComponentB(*components)
