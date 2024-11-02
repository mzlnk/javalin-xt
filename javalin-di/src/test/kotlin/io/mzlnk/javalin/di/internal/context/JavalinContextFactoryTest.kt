package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
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
        class ComponentB
        class ComponentA(val componentB: ComponentB)

        // and:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
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
        val componentA = context.findSingleton(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.findSingleton(identifier(ComponentB::class.java)) ?: fail("Component B not found")

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
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentC::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentC) }
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
        val componentA = context.findSingleton(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.findSingleton(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.findSingleton(identifier(ComponentC::class.java)) ?: fail("Component C not found")

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
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(identifier(ComponentD::class.java)),
            instanceProvider = { ComponentC(it[0] as ComponentD) }
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
        val componentA = context.findSingleton(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.findSingleton(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.findSingleton(identifier(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.findSingleton(identifier(ComponentD::class.java)) ?: fail("Component D not found")

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
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java), identifier(ComponentC::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
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
        val componentA = context.findSingleton(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.findSingleton(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.findSingleton(identifier(ComponentC::class.java)) ?: fail("Component C not found")

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
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java), identifier(ComponentC::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentD::class.java), identifier(ComponentE::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentD, it[1] as ComponentE) }
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
        val componentA = context.findSingleton(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.findSingleton(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.findSingleton(identifier(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.findSingleton(identifier(ComponentD::class.java)) ?: fail("Component D not found")
        val componentE = context.findSingleton(identifier(ComponentE::class.java)) ?: fail("Component E not found")

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
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentC::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentC::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentC) }
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
        val componentA = context.findSingleton(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.findSingleton(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.findSingleton(identifier(ComponentC::class.java)) ?: fail("Component C not found")

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
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(identifier(ComponentB::class.java), identifier(ComponentC::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(identifier(ComponentD::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentD) }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(identifier(ComponentD::class.java)),
            instanceProvider = { ComponentC(it[0] as ComponentD) }
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
        val componentA = context.findSingleton(identifier(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.findSingleton(identifier(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.findSingleton(identifier(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.findSingleton(identifier(ComponentD::class.java)) ?: fail("Component D not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentA.componentC).isEqualTo(componentC)
        assertThat(componentB.componentD).isEqualTo(componentD)
        assertThat(componentC.componentD).isEqualTo(componentD)
    }

    @Test
    fun `should throw exception if there are multiple candidates for given dependency`() {
        // given:
        class ComponentA
        class ComponentB(val componentA: ComponentA)

        // and:
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
            instanceProvider = { ComponentB(it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2, singletonB)

        // when:
        val exception = assertThatThrownBy {
            JavalinContextFactory(source = {definitions}).create()
        }

        // then:
        exception.isInstanceOf(JavalinContextException::class.java)
        exception.hasMessage("Multiple candidates found for io.mzlnk.javalin.di.internal.context.JavalinContextFactoryTest\$should throw exception if there are multiple candidates for given dependency\$ComponentA")
    }
    
    private companion object {
        
        fun <T> identifier(type: Class<T>): SingletonDefinition.Identifier<T> = SingletonDefinition.Identifier.Single(type = type)
        
    }

}
