package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.context.ApplicationContextException
import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
import io.mzlnk.javalin.xt.context.TypeReference
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SingletonDefinitionContextTest {

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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")

        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        val componentC = context.findInstance(identifier(ComponentC::class.java)) ?: Assertions.fail("Component C not found")

        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
        Assertions.assertThat(componentB.components["C"]).isEqualTo(componentC)
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(4)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        val componentC = context.findInstance(identifier(ComponentC::class.java)) ?: Assertions.fail("Component C not found")
        val componentD = context.findInstance(identifier(ComponentD::class.java)) ?: Assertions.fail("Component D not found")

        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
        Assertions.assertThat(componentC.components["D"]).isEqualTo(componentD)
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        val componentC = context.findInstance(identifier(ComponentC::class.java)) ?: Assertions.fail("Component C not found")

        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
        Assertions.assertThat(componentA.components["C"]).isEqualTo(componentC)
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        val componentC = context.findInstance(identifier(ComponentC::class.java)) ?: Assertions.fail("Component C not found")

        Assertions.assertThat(componentB.components["A"]).isEqualTo(componentA)
        Assertions.assertThat(componentC.components["A"]).isEqualTo(componentA)
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(5)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        val componentC = context.findInstance(identifier(ComponentC::class.java)) ?: Assertions.fail("Component C not found")
        val componentD = context.findInstance(identifier(ComponentD::class.java)) ?: Assertions.fail("Component D not found")
        val componentE = context.findInstance(identifier(ComponentE::class.java)) ?: Assertions.fail("Component E not found")

        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
        Assertions.assertThat(componentA.components["C"]).isEqualTo(componentC)
        Assertions.assertThat(componentB.components["D"]).isEqualTo(componentD)
        Assertions.assertThat(componentB.components["E"]).isEqualTo(componentE)
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(4)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        val componentC = context.findInstance(identifier(ComponentC::class.java)) ?: Assertions.fail("Component C not found")
        val componentD = context.findInstance(identifier(ComponentD::class.java)) ?: Assertions.fail("Component D not found")

        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
        Assertions.assertThat(componentA.components["C"]).isEqualTo(componentC)
        Assertions.assertThat(componentB.components["D"]).isEqualTo(componentD)
        Assertions.assertThat(componentC.components["D"]).isEqualTo(componentD)
    }

    @Test
    fun `should inject a candidate to component dependency if defined by given type`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val componentB = ComponentB()

        // and:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB }
        )

        // and:
        val definitions = listOf(singletonA, singletonB)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
    }

    @Test
    fun `should inject a candidate to component dependency if defined by supertype`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val componentB1 = ComponentB1()

        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = identifier(ComponentB1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB1 }
        )

        // and:
        val definitions = listOf(singletonA, singletonB1)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB1)
    }

    @Test
    fun `should inject a candidate to component dependency if defined by interface type`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val componentB = ComponentB()

        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(TypeB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as TypeB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB }
        )

        // and:
        val definitions = listOf(singletonA, singletonB)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(componentA.components["B"]).isEqualTo(componentB)
    }

    @Test
    fun `should inject list of candidates to component dependency if list defined by given type`() {
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(componentA.components["Bs"] as List<ComponentB>)
            .containsExactlyInAnyOrder(componentB1, componentB2)
    }

    @Test
    fun `should inject list of candidates to component dependency if list defined by super type`() {
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(componentA.components["Bs"] as List<ComponentB>)
            .containsExactlyInAnyOrder(componentB1, componentB2)
    }

    @Test

    fun `should inject list of candidates to component dependency if list defined by interface type`() {
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(componentA.components["Bs"] as List<ComponentB>)
            .containsExactlyInAnyOrder(componentB1, componentB2)
    }

    @Test
    fun `should inject empty list to component dependency if no candidate provided`() {
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val componentA = context.findInstance(identifier(ComponentA::class.java)) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(componentA.components["Bs"] as List<ComponentB>).isEmpty()
    }

    @Test
    fun `should inject list of candidates to component dependency if list defined explicitly`() {
        // given:
        val componentA1 = ComponentA()
        val componentA2 = ComponentA()

        // and:
        val singletonsA1A2 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}),
            dependencies = emptyList(),
            instanceProvider = { listOf(componentA1, componentA2) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(object : TypeReference<List<ComponentA>>() {})),
            instanceProvider = { ComponentB("As" to it[0] as List<ComponentA>) }
        )

        // and:
        val definitions = listOf(singletonsA1A2, singletonB)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        Assertions.assertThat(componentB.components["As"] as List<ComponentA>)
            .containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should inject list of singletons defined one by one instead of explicit list if defined both`() {
        // given:
        val componentA1 = ComponentA()
        val componentA2 = ComponentA()

        // and:
        val componentA3 = ComponentA()
        val componentA4 = ComponentA()

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

        val singletonsA3A4 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}),
            dependencies = emptyList(),
            instanceProvider = { listOf(componentA3, componentA4) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(object : TypeReference<List<ComponentA>>() {})),
            instanceProvider = { ComponentB("As" to it[0] as List<ComponentA>) }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2, singletonsA3A4, singletonB)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val componentB = context.findInstance(identifier(ComponentB::class.java)) ?: Assertions.fail("Component B not found")
        Assertions.assertThat(componentB.components["As"] as List<ComponentA>)
            .containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should get singleton by its type`() {
        // given:
        val componentA = ComponentA()
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.findInstance(identifier(ComponentA::class.java))).isEqualTo(componentA)
    }

    @Test
    fun `should get singleton by its type with generic type`() {
        // given:
        val componentG = ComponentG<String>()

        // and:
        val singletonG = SingletonDefinition(
            identifier = identifier(object : TypeReference<ComponentG<String>>() {}),
            dependencies = emptyList(),
            instanceProvider = { componentG }
        )

        // and:
        val definitions = listOf(singletonG)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.findInstance(identifier(object : TypeReference<ComponentG<String>>() {}))).isEqualTo(
            componentG
        )
    }

    @Test
    fun `should get singleton by its supertype`() {
        // given:
        val componentA1 = ComponentA1()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA1 }
        )

        // and:
        val definitions = listOf(singletonA1)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.findInstance(identifier(ComponentA::class.java))).isEqualTo(componentA1)
    }

    @Test
    fun `should get singleton by its implemented interface type`() {
        // given:
        val componentB = ComponentB()

        // and:
        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB }
        )

        // and:
        val definitions = listOf(singletonB)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.findInstance(identifier(TypeB::class.java))).isEqualTo(componentB)
    }

    @Test
    fun `should get null if there is no candidate for given type`() {
        // given:
        // no definitions for ComponentA

        // and:
        val definitions = emptyList<SingletonDefinition<*>>()

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        Assertions.assertThat(context.findInstance(identifier(ComponentA::class.java))).isNull()
    }

    @Test
    fun `should get all singletons by type`() {
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val components = context.findInstance(identifier(object : TypeReference<List<ComponentA>>() {}))
            ?: Assertions.fail("Components not found")

        Assertions.assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should get all singletons by supertype`() {
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val components = context.findInstance(identifier(object : TypeReference<List<ComponentA>>() {}))
            ?: Assertions.fail("Components not found")

        Assertions.assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should get all singletons by implemented interface type`() {
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
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val components = context.findInstance(identifier(object : TypeReference<List<TypeA>>() {}))
            ?: Assertions.fail("Components not found")

        Assertions.assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should get empty list if there are no candidates for given type`() {
        // given:
        // no definitions for ComponentA

        // and:
        val definitions = emptyList<SingletonDefinition<*>>()

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val components = context.findInstance(identifier(object : TypeReference<List<ComponentA>>() {}))
            ?: Assertions.fail("Components not found")

        Assertions.assertThat(components).isEmpty()
    }

    @Test
    fun `should get list of singletons if definition explicitly provides it as list`() {
        // given:
        val componentA1 = ComponentA()
        val componentA2 = ComponentA()

        // and:
        val singletonsA1A2 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}),
            dependencies = emptyList(),
            instanceProvider = { listOf(componentA1, componentA2) }
        )

        // and:
        val definitions = listOf(singletonsA1A2)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val components = context.findInstance(identifier(object : TypeReference<List<ComponentA>>() {}))
        Assertions.assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should get list of singletons defined one by one instead of explicit list if defined both`() {
        // given:
        val componentA1 = ComponentA()
        val componentA2 = ComponentA()

        // and:
        val componentA3 = ComponentA()
        val componentA4 = ComponentA()

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

        val singletonsA3A4 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}),
            dependencies = emptyList(),
            instanceProvider = { listOf(componentA3, componentA4) }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2, singletonsA3A4)

        // when:
        val context = SingletonDefinitionContext.create(definitions)

        // then:
        val components = context.findInstance(identifier(object : TypeReference<List<ComponentA>>() {}))
        Assertions.assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should throw exception if there are defined multiple explicit lists of singletons for given type`() {
        // given:
        val singletonsA1A2 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}),
            dependencies = emptyList(),
            instanceProvider = { listOf(ComponentA(), ComponentA()) }
        )

        val singletonsA3A4 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}),
            dependencies = emptyList(),
            instanceProvider = { listOf(ComponentA(), ComponentA()) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(object : TypeReference<List<ComponentA>>() {})),
            instanceProvider = { ComponentB("As" to it[0] as List<ComponentA>) }
        )

        // and:
        val definitions = listOf(singletonsA1A2, singletonsA3A4, singletonB)

        // when:
        val exception = Assertions.assertThatThrownBy {
            SingletonDefinitionContext.create(definitions)
        }

        // then:
        exception.isInstanceOf(ApplicationContextException::class.java)
        exception.hasMessage("Multiple candidates found for java.util.List<? extends io.mzlnk.javalin.xt.internal.context.ComponentA>")
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
        val exception = Assertions.assertThatThrownBy {
            SingletonDefinitionContext.create(definitions)
        }

        // then:
        exception.isInstanceOf(ApplicationContextException::class.java)
        exception.hasMessage("Multiple candidates found for io.mzlnk.javalin.xt.internal.context.ComponentA")
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
        val exception = Assertions.assertThatThrownBy {
            SingletonDefinitionContext.create(definitions)
        }

        // then:
        exception.isInstanceOf(ApplicationContextException::class.java)
        exception.hasMessage("No candidates found for io.mzlnk.javalin.xt.internal.context.ComponentA")
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
        val exception = Assertions.assertThatThrownBy {
            SingletonDefinitionContext.create(definitions)
        }

        // then:
        exception.isInstanceOf(ApplicationContextException::class.java)
        exception.hasMessage(
            """
            Failed to create context due to dependency cycle(s):
            Cycle #1:
            ┌->io.mzlnk.javalin.xt.internal.context.ComponentA -> io.mzlnk.javalin.xt.internal.context.ComponentC -> io.mzlnk.javalin.xt.internal.context.ComponentB -┐
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
// @formatter:off
private open class ComponentA(vararg components: Pair<String, Any>) : TypeA { val components = mapOf(*components) }
private open class ComponentB(vararg components: Pair<String, Any>) : TypeB { val components = mapOf(*components) }
private open class ComponentC(vararg components: Pair<String, Any>) { val components = mapOf(*components) }
private open class ComponentD(vararg components: Pair<String, Any>) { val components = mapOf(*components) }
private open class ComponentE(vararg components: Pair<String, Any>) { val components = mapOf(*components) }
private open class ComponentG<T>(vararg components: Pair<String, Any>) { val components = mapOf(*components) }
// @formatter:on

private class ComponentA1(vararg components: Pair<String, Any>) : ComponentA(*components)
private class ComponentA2(vararg components: Pair<String, Any>) : ComponentA(*components)

private class ComponentB1(vararg components: Pair<String, Any>) : ComponentB(*components)
private class ComponentB2(vararg components: Pair<String, Any>) : ComponentB(*components)