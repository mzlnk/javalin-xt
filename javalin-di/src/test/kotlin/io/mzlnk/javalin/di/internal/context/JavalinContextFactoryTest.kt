@file:Suppress("UNCHECKED_CAST")

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
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getSingleton(single(ComponentB::class.java)) ?: fail("Component B not found")

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
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = listOf(single(ComponentC::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentC) }
        )

        val singletonC = SingletonDefinition(
            identifier = single(ComponentC::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getSingleton(single(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getSingleton(single(ComponentC::class.java)) ?: fail("Component C not found")

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
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            identifier = single(ComponentC::class.java),
            dependencies = listOf(single(ComponentD::class.java)),
            instanceProvider = { ComponentC(it[0] as ComponentD) }
        )

        val singletonD = SingletonDefinition(
            identifier = single(ComponentD::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getSingleton(single(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getSingleton(single(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.getSingleton(single(ComponentD::class.java)) ?: fail("Component D not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentC.componentD).isEqualTo(componentD)
    }

    @Test
    fun `should create context for component having multiple dependencies`() {
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
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(ComponentB::class.java), single(ComponentC::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            identifier = single(ComponentC::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getSingleton(single(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getSingleton(single(ComponentC::class.java)) ?: fail("Component C not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentA.componentC).isEqualTo(componentC)
    }

    @Test
    fun `should create context for component being dependency for multiple components`() {
        /*
         * dependency graph:
         * C <- A -> B
         */

        // given:
        class ComponentA
        class ComponentB(val componentA: ComponentA)
        class ComponentC(val componentA: ComponentA)

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = listOf(single(ComponentA::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentA) }
        )

        val singletonC = SingletonDefinition(
            identifier = single(ComponentC::class.java),
            dependencies = listOf(single(ComponentA::class.java)),
            instanceProvider = { ComponentC(it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.getSingleton(single(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getSingleton(single(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getSingleton(single(ComponentC::class.java)) ?: fail("Component C not found")

        assertThat(componentB.componentA).isEqualTo(componentA)
        assertThat(componentC.componentA).isEqualTo(componentA)
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
        class ComponentC
        class ComponentD
        class ComponentE
        class ComponentB(val componentD: ComponentD, val componentE: ComponentE)
        class ComponentA(val componentB: ComponentB, val componentC: ComponentC)

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(ComponentB::class.java), single(ComponentC::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = listOf(single(ComponentD::class.java), single(ComponentE::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentD, it[1] as ComponentE) }
        )

        val singletonC = SingletonDefinition(
            identifier = single(ComponentC::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentC() }
        )

        val singletonD = SingletonDefinition(
            identifier = single(ComponentD::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentD() }
        )

        val singletonE = SingletonDefinition(
            identifier = single(ComponentE::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getSingleton(single(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getSingleton(single(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.getSingleton(single(ComponentD::class.java)) ?: fail("Component D not found")
        val componentE = context.getSingleton(single(ComponentE::class.java)) ?: fail("Component E not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentA.componentC).isEqualTo(componentC)
        assertThat(componentB.componentD).isEqualTo(componentD)
        assertThat(componentB.componentE).isEqualTo(componentE)
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
        class ComponentD
        class ComponentC(val componentD: ComponentD)
        class ComponentB(val componentD: ComponentD)
        class ComponentA(val componentB: ComponentB, val componentC: ComponentC)

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(ComponentB::class.java), single(ComponentC::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB, it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = listOf(single(ComponentD::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentD) }
        )

        val singletonC = SingletonDefinition(
            identifier = single(ComponentC::class.java),
            dependencies = listOf(single(ComponentD::class.java)),
            instanceProvider = { ComponentC(it[0] as ComponentD) }
        )

        val singletonD = SingletonDefinition(
            identifier = single(ComponentD::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java)) ?: fail("Component A not found")
        val componentB = context.getSingleton(single(ComponentB::class.java)) ?: fail("Component B not found")
        val componentC = context.getSingleton(single(ComponentC::class.java)) ?: fail("Component C not found")
        val componentD = context.getSingleton(single(ComponentD::class.java)) ?: fail("Component D not found")

        assertThat(componentA.componentB).isEqualTo(componentB)
        assertThat(componentA.componentC).isEqualTo(componentC)
        assertThat(componentB.componentD).isEqualTo(componentD)
        assertThat(componentC.componentD).isEqualTo(componentD)
    }

    @Test
    fun `should create context for component having dependency which is supertype`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        open class ComponentB
        class ComponentB1 : ComponentB()
        class ComponentA(val componentB: ComponentB)

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as ComponentB) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = single(ComponentB1::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java))
        val componentB1 = context.getSingleton(single(ComponentB1::class.java))

        assertThat(componentA.componentB).isEqualTo(componentB1)
    }

    @Test
    fun `should create context for component having dependency which interface type`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        class ComponentB : TypeB
        class ComponentA(val componentB: TypeB)

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(single(TypeB::class.java)),
            instanceProvider = { ComponentA(it[0] as TypeB) }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java))
        val componentB = context.getSingleton(single(ComponentB::class.java))

        assertThat(componentA.componentB).isEqualTo(componentB)
    }
    
    @Test
    fun `should create context for component having iterable dependency of given type`() {
        /*
         * dependency graph:
         * A <- B
         */
        
        // given:
        class ComponentB
        class ComponentA(val bComponents: List<ComponentB>)

        // and:
        val componentB1 = ComponentB()
        val componentB2 = ComponentB()

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(iterable(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as List<ComponentB>) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB1 }
        )

        val singletonB2 = SingletonDefinition(
            identifier = single(ComponentB::class.java),
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
        val componentA = context.getSingleton(single(ComponentA::class.java))
        assertThat(componentA.bComponents).containsExactlyInAnyOrder(componentB1, componentB2)
    }
    
    @Test
    fun `should create context for component having iterable dependency of supertype`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        open class ComponentB
        class ComponentB1 : ComponentB()
        class ComponentB2 : ComponentB()
        class ComponentA(val bComponents: List<ComponentB>)

        // and:
        val componentB1 = ComponentB1()
        val componentB2 = ComponentB2()

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(iterable(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as List<ComponentB>) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = single(ComponentB1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB1 }
        )

        val singletonB2 = SingletonDefinition(
            identifier = single(ComponentB2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB2 }
        )

        // and:
        val definitions = listOf(singletonA, singletonB1, singletonB2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA = context.getSingleton(single(ComponentA::class.java))
        assertThat(componentA.bComponents).containsExactlyInAnyOrder(componentB1, componentB2)
    }
    
    @Test
    fun `should create context for component having iterable dependency of interface type`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        class ComponentB1 : TypeB
        class ComponentB2 : TypeB
        class ComponentA(val bComponents: List<TypeB>)

        // and:
        val componentB1 = ComponentB1()
        val componentB2 = ComponentB2()

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(iterable(TypeB::class.java)),
            instanceProvider = { ComponentA(it[0] as List<TypeB>) }
        )

        val singletonB1 = SingletonDefinition(
            identifier = single(ComponentB1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB1 }
        )

        val singletonB2 = SingletonDefinition(
            identifier = single(ComponentB2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentB2 }
        )

        // and:
        val definitions = listOf(singletonA, singletonB1, singletonB2)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA = context.getSingleton(single(ComponentA::class.java))
        assertThat(componentA.bComponents).containsExactlyInAnyOrder(componentB1, componentB2)
    }

    @Test
    fun `should create context component having iterable dependency of type but no candidates`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        class ComponentB
        class ComponentA(val bComponents: List<ComponentB>)

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = listOf(iterable(ComponentB::class.java)),
            instanceProvider = { ComponentA(it[0] as List<ComponentB>) }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA = context.getSingleton(single(ComponentA::class.java))
        assertThat(componentA.bComponents).isEmpty()
    }

    @Test
    fun `should access singleton by its type`() {
        // given:
        class ComponentA

        // and:
        val singletonA = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        assertThatCode { context.getSingleton(single(ComponentA::class.java)) }.doesNotThrowAnyException()
    }

    @Test
    fun `should access the same singleton by its supertype`() {
        // given:
        open class ComponentA
        class ComponentA1 : ComponentA()

        // and:
        val singletonB1 = SingletonDefinition(
            identifier = single(ComponentA1::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA1() }
        )

        // and:
        val definitions = listOf(singletonB1)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentA1 = context.getSingleton(single(ComponentA1::class.java))
        val componentA = context.getSingleton(single(ComponentA::class.java))

        assertThat(componentA1).isEqualTo(componentA)
    }

    @Test
    fun `should access the same singleton by implemented type`() {
        // given:
        class ComponentB : TypeB

        // and:
        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        // and:
        val definitions = listOf(singletonB)

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val componentB = context.getSingleton(single(ComponentB::class.java))
        val typeB = context.getSingleton(single(TypeB::class.java))

        assertThat(componentB).isEqualTo(typeB)
    }
    
    @Test
    fun `should access all singletons by type`() {
        // given:
        class ComponentA
        
        // and:
        val componentA1 = ComponentA()
        val componentA2 = ComponentA()
        
        // and:
        val singletonA1 = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA1 }
        )
        
        val singletonA2 = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA2 }
        )
        
        // and:
        val definitions = listOf(singletonA1, singletonA2)
        
        // when:
        val context = JavalinContextFactory(source = { definitions }).create()
        
        // then:
        val components = context.getSingletonList(iterable(ComponentA::class.java))
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)    
    }
    
    @Test
    fun `should access all singletons by supertype`() {
        // given:
        open class ComponentA
        class ComponentA1 : ComponentA()
        class ComponentA2 : ComponentA()
        
        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()
        
        // and:
        val singletonA1 = SingletonDefinition(
            identifier = single(ComponentA1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA1}
        )
        
        val singletonA2 = SingletonDefinition(
            identifier = single(ComponentA2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA2 }
        )
        
        // and:
        val definitions = listOf(singletonA1, singletonA2)
        
        // when:
        val context = JavalinContextFactory(source = { definitions }).create()
        
        // then:
        val components = context.getSingletonList(iterable(ComponentA::class.java))
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }
    
    @Test
    fun `should access all singletons by implemented interface`() {
        // given:
        class ComponentA1 : TypeA
        class ComponentA2 : TypeA
        
        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()
        
        // and:
        val singletonA1 = SingletonDefinition(
            identifier = single(ComponentA1::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA1 }
        )
        
        val singletonA2 = SingletonDefinition(
            identifier = single(ComponentA2::class.java),
            dependencies = emptyList(),
            instanceProvider = { componentA2 }
        )
        
        // and:
        val definitions = listOf(singletonA1, singletonA2)
        
        // when:
        val context = JavalinContextFactory(source = { definitions }).create()
        
        // then:
        val components = context.getSingletonList(iterable(TypeA::class.java))
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should access no singletons if there are no candidates for given type`() {
        // given:
        class ComponentA

        // and:
        // no definitions for ComponentA

        // and:
        val definitions = emptyList<SingletonDefinition<*>>()

        // when:
        val context = JavalinContextFactory(source = { definitions }).create()

        // then:
        val components = context.getSingletonList(iterable(ComponentA::class.java))
        assertThat(components).isEmpty()
    }

    @Test
    fun `should throw exception if there are multiple candidates for given component dependency`() {
        // given:
        class ComponentA
        class ComponentB(val componentA: ComponentA)

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        val singletonA2 = SingletonDefinition(
            identifier = single(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = listOf(single(ComponentA::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2, singletonB)

        // when:
        val exception = assertThatThrownBy {
            JavalinContextFactory(source = { definitions }).create()
        }

        // then:
        exception.isInstanceOf(JavalinContextException::class.java)
        exception.hasMessage("Multiple candidates found for io.mzlnk.javalin.di.internal.context.JavalinContextFactoryTest\$should throw exception if there are multiple candidates for given component dependency\$ComponentA")
    }

    @Test
    fun `should throw exception if there are no candidates for given component dependency`() {
        // given:
        class ComponentA
        class ComponentB(val componentA: ComponentA)

        // and:
        // no definition for ComponentA

        val singletonB = SingletonDefinition(
            identifier = single(ComponentB::class.java),
            dependencies = listOf(single(ComponentA::class.java)),
            instanceProvider = { ComponentB(it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonB)

        // when:
        val exception = assertThatThrownBy {
            JavalinContextFactory(source = { definitions }).create()
        }

        // then:
        exception.isInstanceOf(JavalinContextException::class.java)
        exception.hasMessage("No candidates found for io.mzlnk.javalin.di.internal.context.JavalinContextFactoryTest\$should throw exception if there are no candidates for given component dependency\$ComponentA")
    }

    @Test
    fun `should throw exception if there is a dependency cycle between components`() {
        /*
         * dependency graph:
         * ┌->A -> B -> C -┐
         * └---------------┘
         */

        // given:
        class Components {

            inner class ComponentA(val componentB: ComponentB)
            inner class ComponentB(val componentC: ComponentC)
            inner class ComponentC(val componentA: ComponentA)

        }

        // and:
        val definitions = listOf(
            SingletonDefinition(
                identifier = single(Components.ComponentA::class.java),
                dependencies = listOf(single(Components.ComponentB::class.java)),
                instanceProvider = { Components().ComponentA(it[0] as Components.ComponentB) }
            ),
            SingletonDefinition(
                identifier = single(Components.ComponentB::class.java),
                dependencies = listOf(single(Components.ComponentC::class.java)),
                instanceProvider = { Components().ComponentB(it[0] as Components.ComponentC) }
            ),
            SingletonDefinition(
                identifier = single(Components.ComponentC::class.java),
                dependencies = listOf(single(Components.ComponentA::class.java)),
                instanceProvider = { Components().ComponentC(it[0] as Components.ComponentA) }
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
            ┌->io.mzlnk.javalin.di.internal.context.JavalinContextFactoryTest${'$'}should throw exception if there is a dependency cycle between components${'$'}Components${'$'}ComponentA -> io.mzlnk.javalin.di.internal.context.JavalinContextFactoryTest${'$'}should throw exception if there is a dependency cycle between components${'$'}Components${'$'}ComponentC -> io.mzlnk.javalin.di.internal.context.JavalinContextFactoryTest${'$'}should throw exception if there is a dependency cycle between components${'$'}Components${'$'}ComponentB -┐
            └---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------┘
            """.trimIndent()
        )
    }

    @Test
    fun `should throw exception when try to register iterable singleton`() {
        // given:
        class ComponentA

        // and:
        val singletonA = SingletonDefinition(
            identifier = iterable(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { listOf(ComponentA()) }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val exception = assertThatThrownBy {
            JavalinContextFactory(source = { definitions }).create()
        }

        // then:
        exception.isInstanceOf(JavalinContextException::class.java)
        exception.hasMessage("Iterable singleton definition `List<io.mzlnk.javalin.di.internal.context.JavalinContextFactoryTest${'$'}should throw exception when try to register iterable singleton${'$'}ComponentA>` is not supported.")
    }

    private companion object {

        // general purpose types for testing
        interface TypeA
        interface TypeB

        fun <T : Any> single(type: Class<T>): SingletonDefinition.Identifier.Single<T> =
            SingletonDefinition.Identifier.Single(type = type)

        fun <T : Any> iterable(type: Class<T>): SingletonDefinition.Identifier.Iterable<T> =
            SingletonDefinition.Identifier.Iterable(type = type)

    }

}
