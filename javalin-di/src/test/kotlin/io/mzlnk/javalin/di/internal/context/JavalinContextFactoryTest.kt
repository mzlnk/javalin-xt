package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class JavalinContextFactoryTest {

    @Test
    fun `should create context for dependent components`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        class ComponentB
        class ComponentA(val componentB: ComponentB)

        // and:
        val singletonA = SingletonDefinition(
            type = ComponentA::class.java,
            dependencies = listOf(ComponentB::class.java),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            type = ComponentB::class.java,
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
        val componentA = context.findSingleton(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findSingleton(ComponentB::class.java) ?: fail("Component B not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
    }

    @Test
    fun `should create context for chain of dependent components`() {
        /*
         * dependency graph:
         * A <- B <- C
         */

        // given:
        class ComponentC
        class ComponentB(val componentC: ComponentC)
        class ComponentA(val componentB: ComponentB)

        // and:
        val singletonA = SingletonDefinition(
            type = ComponentA::class.java,
            dependencies = listOf(ComponentB::class.java),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            type = ComponentB::class.java,
            dependencies = listOf(ComponentC::class.java),
            instanceProvider = { ComponentB(it[0] as ComponentC) }
        )

        val singletonC = SingletonDefinition(
            type = ComponentC::class.java,
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
        val componentA = context.findSingleton(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findSingleton(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findSingleton(ComponentC::class.java) ?: fail("Component C not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentB.componentC).isEqualTo(componentC)
    }

    @Test
    fun `should create context for multiple component dependency trees`() {
        /*
         * dependency graph:
         * A <- B
         * C <- D
         */

        // given:
        class ComponentB
        class ComponentA(val componentB: ComponentB)

        class ComponentD
        class ComponentC(val componentD: ComponentD)

        // and:
        val singletonA = SingletonDefinition(
            type = ComponentA::class.java,
            dependencies = listOf(ComponentB::class.java),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            type = ComponentB::class.java,
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            type = ComponentC::class.java,
            dependencies = listOf(ComponentD::class.java),
            instanceProvider = { ComponentC(it[0] as ComponentD) }
        )

        val singletonD = SingletonDefinition(
            type = ComponentD::class.java,
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
        val componentA = context.findSingleton(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findSingleton(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findSingleton(ComponentC::class.java) ?: fail("Component C not found")
        val componentD = context.findSingleton(ComponentD::class.java) ?: fail("Component D not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentC.componentD).isEqualTo(componentD)
    }

    @Test
    fun `should create context for multiple dependencies for one component`() {
        /*
         * dependency graph:
         * C -> A <- B
         */

        // given:
        class ComponentB
        class ComponentC
        class ComponentA(val componentB: ComponentB, val componentC: ComponentC)

        // and:
        val singletonA = SingletonDefinition(
            type = ComponentA::class.java,
            dependencies = listOf(ComponentB::class.java, ComponentC::class.java),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            type = ComponentB::class.java,
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            type = ComponentC::class.java,
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
        val componentA = context.findSingleton(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findSingleton(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findSingleton(ComponentC::class.java) ?: fail("Component C not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentA.componentC).isEqualTo(componentC)
    }

    @Test
    fun `should create context for multiple dependencies for multiple components in chain`() {
        /*
         * dependency graph:
         * C -> A
         *      ^
         *      |
         * D -> B <- E
         */

        // given:
        class ComponentC
        class ComponentD
        class ComponentE
        class ComponentB(val componentD: ComponentD, val componentE: ComponentE)
        class ComponentA(val componentB: ComponentB, val componentC: ComponentC)

        // and:
        val singletonA = SingletonDefinition(
            type = ComponentA::class.java,
            dependencies = listOf(ComponentB::class.java, ComponentC::class.java),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            type = ComponentB::class.java,
            dependencies = listOf(ComponentD::class.java, ComponentE::class.java),
            instanceProvider = { ComponentB(it[0] as ComponentD, it[1] as ComponentE) }
        )

        val singletonC = SingletonDefinition(
            type = ComponentC::class.java,
            dependencies = emptyList(),
            instanceProvider = { ComponentC() }
        )

        val singletonD = SingletonDefinition(
            type = ComponentD::class.java,
            dependencies = emptyList(),
            instanceProvider = { ComponentD() }
        )

        val singletonE = SingletonDefinition(
            type = ComponentE::class.java,
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
        val componentA = context.findSingleton(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findSingleton(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findSingleton(ComponentC::class.java) ?: fail("Component C not found")
        val componentD = context.findSingleton(ComponentD::class.java) ?: fail("Component D not found")
        val componentE = context.findSingleton(ComponentE::class.java) ?: fail("Component E not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentA.componentC).isEqualTo(componentC)
        assertThat(componentB.componentD).isEqualTo(componentD)
        assertThat(componentB.componentE).isEqualTo(componentE)
    }

    @Test
    fun `should create context for component dependent to multiple components`() {
        /*
         * dependency graph:
         * A <- C -> B
         */

        // given:
        class ComponentC
        class ComponentA(val componentC: ComponentC)
        class ComponentB(val componentC: ComponentC)

        // and:
        val singletonA = SingletonDefinition(
            type = ComponentA::class.java,
            dependencies = listOf(ComponentC::class.java),
            instanceProvider = { ComponentA(it[0] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            type = ComponentB::class.java,
            dependencies = listOf(ComponentC::class.java),
            instanceProvider = { ComponentB(it[0] as ComponentC) }
        )

        val singletonC = SingletonDefinition(
            type = ComponentC::class.java,
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
        val componentA = context.findSingleton(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findSingleton(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findSingleton(ComponentC::class.java) ?: fail("Component C not found")

        assertThat(componentA.componentC).isEqualTo(componentC)
        assertThat(componentB.componentC).isEqualTo(componentC)
    }

    @Test
    fun `should create context for diamond shaped dependencies`() {
        /*
         * dependency graph:
         *   -> B ->
         * D         A
         *   -> C ->
         */

        // given:
        class ComponentD
        class ComponentC(val componentD: ComponentD)
        class ComponentB(val componentD: ComponentD)
        class ComponentA(val componentB: ComponentB, val componentC: ComponentC)

        // and:
        val singletonA = SingletonDefinition(
            type = ComponentA::class.java,
            dependencies = listOf(ComponentB::class.java, ComponentC::class.java),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            type = ComponentB::class.java,
            dependencies = listOf(ComponentD::class.java),
            instanceProvider = { ComponentB(it[0] as ComponentD) }
        )

        val singletonC = SingletonDefinition(
            type = ComponentC::class.java,
            dependencies = listOf(ComponentD::class.java),
            instanceProvider = { ComponentC(it[0] as ComponentD) }
        )

        val singletonD = SingletonDefinition(
            type = ComponentD::class.java,
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
        val componentA = context.findSingleton(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findSingleton(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findSingleton(ComponentC::class.java) ?: fail("Component C not found")
        val componentD = context.findSingleton(ComponentD::class.java) ?: fail("Component D not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentA.componentC).isEqualTo(componentC)
        assertThat(componentB.componentD).isEqualTo(componentD)
        assertThat(componentC.componentD).isEqualTo(componentD)
    }

}
