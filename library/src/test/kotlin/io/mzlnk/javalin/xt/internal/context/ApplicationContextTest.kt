@file:Suppress("UNCHECKED_CAST")

package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.JavalinXtConfiguration
import io.mzlnk.javalin.xt.context.ApplicationContextException
import io.mzlnk.javalin.xt.context.TypeReference
import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
import io.mzlnk.javalin.xt.internal.properties.*
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.properties.Property
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

class ApplicationContextTest {

    @Test
    fun `should create context for dependent components`() {
        /*
         * dependency graph:
         * A <- B
         */

        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(singletonDependency(ComponentB::class.java)),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA =
            context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        val componentB =
            context.findInstance(ComponentB::class.java) ?: fail("Component B not found")

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
            dependencies = listOf(singletonDependency(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(singletonDependency(ComponentC::class.java)),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findInstance(ComponentC::class.java) ?: fail("Component C not found")

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
            dependencies = listOf(singletonDependency(ComponentB::class.java)),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentB() }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(singletonDependency(ComponentD::class.java)),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(4)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findInstance(ComponentC::class.java) ?: fail("Component C not found")
        val componentD = context.findInstance(ComponentD::class.java) ?: fail("Component D not found")

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
            dependencies = listOf(
                singletonDependency(ComponentB::class.java),
                singletonDependency(ComponentC::class.java)
            ),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findInstance(ComponentC::class.java) ?: fail("Component C not found")

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
            dependencies = listOf(singletonDependency(ComponentA::class.java)),
            instanceProvider = { ComponentB("A" to it[0] as ComponentA) }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(singletonDependency(ComponentA::class.java)),
            instanceProvider = { ComponentC("A" to it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonA, singletonB, singletonC)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findInstance(ComponentC::class.java) ?: fail("Component C not found")

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
            dependencies = listOf(
                singletonDependency(ComponentB::class.java),
                singletonDependency(ComponentC::class.java)
            ),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB, "C" to it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(
                singletonDependency(ComponentD::class.java),
                singletonDependency(ComponentE::class.java)
            ),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(5)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findInstance(ComponentC::class.java) ?: fail("Component C not found")
        val componentD = context.findInstance(ComponentD::class.java) ?: fail("Component D not found")
        val componentE = context.findInstance(ComponentE::class.java) ?: fail("Component E not found")

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
            dependencies = listOf(
                singletonDependency(ComponentB::class.java),
                singletonDependency(ComponentC::class.java)
            ),
            instanceProvider = { ComponentA("B" to it[0] as ComponentB, "C" to it[1] as ComponentC) }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentB::class.java),
            dependencies = listOf(singletonDependency(ComponentD::class.java)),
            instanceProvider = { ComponentB("D" to it[0] as ComponentD) }
        )

        val singletonC = SingletonDefinition(
            identifier = identifier(ComponentC::class.java),
            dependencies = listOf(singletonDependency(ComponentD::class.java)),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(4)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        val componentC = context.findInstance(ComponentC::class.java) ?: fail("Component C not found")
        val componentD = context.findInstance(ComponentD::class.java) ?: fail("Component D not found")

        assertThat(componentA.components["B"]).isEqualTo(componentB)
        assertThat(componentA.components["C"]).isEqualTo(componentC)
        assertThat(componentB.components["D"]).isEqualTo(componentD)
        assertThat(componentC.components["D"]).isEqualTo(componentD)
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
            dependencies = listOf(singletonDependency(ComponentB::class.java)),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(componentA.components["B"]).isEqualTo(componentB)
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
            dependencies = listOf(singletonDependency(ComponentB::class.java)),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(componentA.components["B"]).isEqualTo(componentB1)
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
            dependencies = listOf(singletonDependency(TypeB::class.java)),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(2)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(componentA.components["B"]).isEqualTo(componentB)
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
            dependencies = listOf(singletonDependency(object : TypeReference<List<ComponentB>>() {})),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(3)

        // and:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(componentA.components["Bs"] as List<ComponentB>)
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
            dependencies = listOf(singletonDependency(object : TypeReference<List<ComponentB>>() {})),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(componentA.components["Bs"] as List<ComponentB>)
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
            dependencies = listOf(singletonDependency(object : TypeReference<List<TypeB>>() {})),
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(componentA.components["Bs"] as List<ComponentB>)
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
            dependencies = listOf(singletonDependency(object : TypeReference<List<ComponentB>>() {})),
            instanceProvider = { ComponentA("Bs" to it[0] as List<ComponentB>) }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val componentA = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(componentA.components["Bs"] as List<ComponentB>).isEmpty()
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
            dependencies = listOf(singletonDependency(object : TypeReference<List<ComponentA>>() {})),
            instanceProvider = { ComponentB("As" to it[0] as List<ComponentA>) }
        )

        // and:
        val definitions = listOf(singletonsA1A2, singletonB)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        assertThat(componentB.components["As"] as List<ComponentA>)
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
            dependencies = listOf(singletonDependency(object : TypeReference<List<ComponentA>>() {})),
            instanceProvider = { ComponentB("As" to it[0] as List<ComponentA>) }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2, singletonsA3A4, singletonB)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val componentB = context.findInstance(ComponentB::class.java) ?: fail("Component B not found")
        assertThat(componentB.components["As"] as List<ComponentA>)
            .containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should inject string property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to StringProperty("value")
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asString,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as String) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo("value")
    }

    @Test
    fun `should inject int property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberProperty(42)
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asInt,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as Int) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(42)
    }

    @Test
    fun `should inject long property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberProperty(42L)
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asLong,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as Long) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(42L)
    }

    @Test
    fun `should inject float property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberProperty(42.0f)
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asFloat,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as Float) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(42.0f)
    }

    @Test
    fun `should inject double property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberProperty(42.0)
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asDouble,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as Double) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(42.0)
    }

    @Test
    fun `should inject boolean property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to BooleanProperty(true)
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asBoolean,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as Boolean) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(true)
    }

    @Test
    fun `should inject string list property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to StringListProperty(listOf("value1", "value2"))
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asStringList,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as List<String>) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(listOf("value1", "value2"))
    }

    @Test
    fun `should inject int list property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberListProperty(listOf(42, 43))
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asIntList,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as List<Int>) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(listOf(42, 43))
    }

    @Test
    fun `should inject long list property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberListProperty(listOf(42L, 43L))
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asLongList,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as List<Long>) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(listOf(42L, 43L))
    }

    @Test
    fun `should inject float list property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberListProperty(listOf(42.0f, 43.0f))
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asFloatList,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as List<Float>) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(listOf(42.0f, 43.0f))
    }

    @Test
    fun `should inject double list property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberListProperty(listOf(42.0, 43.0))
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asDoubleList,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as List<Double>) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(listOf(42.0, 43.0))
    }

    @Test
    fun `should inject boolean list property to component dependency`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to BooleanListProperty(listOf(true, false))
        )

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asBooleanList,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as List<Boolean>) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isEqualTo(listOf(true, false))
    }

    @Test
    fun `should inject null to component dependency if property definition marked as optional and property value not provided`() {
        // given:
        val properties = TestApplicationProperties()

        // and:
        val singleton = SingletonDefinition(
            identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asString,
                    required = false
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as String?) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: fail("Component A not found")
        assertThat(component.components["property"]).isNull()
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA)
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.findInstance(object : TypeReference<ComponentG<String>>() {}))
            .isEqualTo(componentG)
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.findInstance(TypeB::class.java)).isEqualTo(componentB)
    }

    @Test
    fun `should get null if there is no candidate for given type`() {
        // given:
        // no definitions for ComponentA

        // and:
        val definitions = emptyList<SingletonDefinition<*>>()

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.findInstance(ComponentA::class.java)).isNull()
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {})
            ?: fail("Components not found")

        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {})
            ?: fail("Components not found")

        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val components = context.findInstance(object : TypeReference<List<TypeA>>() {})
            ?: fail("Components not found")

        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }

    @Test
    fun `should get empty list if there are no candidates for given type`() {
        // given:
        // no definitions for ComponentA

        // and:
        val definitions = emptyList<SingletonDefinition<*>>()

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {})
            ?: fail("Components not found")

        assertThat(components).isEmpty()
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {})
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
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
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {})
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
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
            dependencies = listOf(singletonDependency(object : TypeReference<List<ComponentA>>() {})),
            instanceProvider = { ComponentB("As" to it[0] as List<ComponentA>) }
        )

        // and:
        val definitions = listOf(singletonsA1A2, singletonsA3A4, singletonB)

        // when:
        val exception = assertThatThrownBy {
            ApplicationContextFactory(
                definitionSource = { definitions },
                propertiesSource = { EmptyApplicationProperties }
            ).create(config { enabled = true })
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
            dependencies = listOf(singletonDependency(ComponentA::class.java)),
            instanceProvider = { ComponentB("A" to it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2, singletonB)

        // when:
        val exception = assertThatThrownBy {
            ApplicationContextFactory(
                definitionSource = { definitions },
                propertiesSource = { EmptyApplicationProperties }
            ).create(config { enabled = true })
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
            dependencies = listOf(singletonDependency(ComponentA::class.java)),
            instanceProvider = { ComponentB("A" to it[0] as ComponentA) }
        )

        // and:
        val definitions = listOf(singletonB)

        // when:
        val exception = assertThatThrownBy {
            ApplicationContextFactory(
                definitionSource = { definitions },
                propertiesSource = { EmptyApplicationProperties }
            ).create(config { enabled = true })
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
                dependencies = listOf(singletonDependency(ComponentB::class.java)),
                instanceProvider = { ComponentA("B" to it[0] as ComponentB) }
            ),
            SingletonDefinition(
                identifier = identifier(ComponentB::class.java),
                dependencies = listOf(singletonDependency(ComponentC::class.java)),
                instanceProvider = { ComponentB("C" to it[0] as ComponentC) }
            ),
            SingletonDefinition(
                identifier = identifier(ComponentC::class.java),
                dependencies = listOf(singletonDependency(ComponentA::class.java)),
                instanceProvider = { ComponentC("A" to it[0] as ComponentA) }
            )
        )

        // when:
        val exception = assertThatThrownBy {
            ApplicationContextFactory(
                definitionSource = { definitions },
                propertiesSource = { EmptyApplicationProperties }
            ).create(config { enabled = true })
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

    @Test
    fun `should throw exception if property dependency marked as required is not found`() {
        // given:
        // no properties
        val properties = EmptyApplicationProperties

        // and:
        val singleton = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asString,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as String) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val exception = assertThatThrownBy {
            ApplicationContextFactory(
                definitionSource = { definitions },
                propertiesSource = { properties }
            ).create(config { enabled = true })
        }

        // then:
        exception.isInstanceOf(ApplicationContextException::class.java)
        exception.hasMessage("Failed to create singleton `io.mzlnk.javalin.xt.internal.context.ComponentA`. Property `property` not found.")
    }

    @Test
    fun `should throw exception if property dependency is different type than declared`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to StringProperty("value")
        )

        // and:
        val singleton = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = listOf(
                propertyDependency(
                    key = "property",
                    valueProvider = Property::asInt,
                    required = true
                )
            ),
            instanceProvider = { ComponentA("property" to it[0] as String) }
        )

        // and:
        val definitions = listOf(singleton)

        // when:
        val exception = assertThatThrownBy {
            ApplicationContextFactory(
                definitionSource = { definitions },
                propertiesSource = { properties }
            ).create(config { enabled = true })
        }

        // then:
        exception.isInstanceOf(ApplicationContextException::class.java)
        exception.hasMessage("Failed to create singleton `io.mzlnk.javalin.xt.internal.context.ComponentA`. Property `property` has invalid type.")
    }

    @Test
    fun `should throw exception if there are multiple candidates for requested component`() {
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

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = true })

        // then:
        val exception = assertThatThrownBy {
            context.findInstance(ComponentA::class.java)
        }

        exception.isInstanceOf(ApplicationContextException::class.java)
        exception.hasMessage("Multiple candidates found for io.mzlnk.javalin.xt.internal.context.ComponentA")
    }

    @Test
    fun `should create empty context when disabled`() {
        // given:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            instanceProvider = { ComponentA() }
        )

        // and:
        val definitions = listOf(singletonA)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create(config { enabled = false })

        // then:
        assertThat(context.size()).isEqualTo(0)
        assertThat(context.findInstance(ComponentA::class.java)).isNull()
    }

    @Test
    fun `should create context for singleton conditional on string property`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to StringProperty("A1")
        )

        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA = SingletonDefinition(
            identifier = identifier(ComponentA1::class.java),
            dependencies = emptyList(),
            conditions = listOf(SingletonDefinition.Condition.OnProperty(property = "property", havingValue = "A1")),
            instanceProvider = { componentA1 }
        )

        val singletonB = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            conditions = listOf(SingletonDefinition.Condition.OnProperty(property = "property", havingValue = "A2")),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA, singletonB)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(1)
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
    }

    @Test
    fun `should create context for singleton conditional on number property`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberProperty(1)
        )

        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            conditions = listOf(SingletonDefinition.Condition.OnProperty(property = "property", havingValue = "1")),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            conditions = listOf(SingletonDefinition.Condition.OnProperty(property = "property", havingValue = "2")),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(1)
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
    }

    @Test
    fun `should create context for singleton conditional on boolean property`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to BooleanProperty(true)
        )

        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            conditions = listOf(SingletonDefinition.Condition.OnProperty(property = "property", havingValue = "true")),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            conditions = listOf(SingletonDefinition.Condition.OnProperty(property = "property", havingValue = "false")),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(1)
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
    }

    @Test
    fun `should create context for singleton conditional on number list property`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to NumberListProperty(listOf(1, 2))
        )

        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            conditions = listOf(
                SingletonDefinition.Condition.OnProperty(
                    property = "property",
                    havingValue = "[1, 2]"
                )
            ),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            conditions = listOf(
                SingletonDefinition.Condition.OnProperty(
                    property = "property",
                    havingValue = "[2, 3]"
                )
            ),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(1)
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
    }

    @Test
    fun `should create context for singleton conditional on string list property`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to StringListProperty(listOf("A", "B"))
        )

        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            conditions = listOf(
                SingletonDefinition.Condition.OnProperty(
                    property = "property",
                    havingValue = "[A, B]"
                )
            ),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            conditions = listOf(
                SingletonDefinition.Condition.OnProperty(
                    property = "property",
                    havingValue = "[B, C]"
                )
            ),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(1)
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
    }

    @Test
    fun `should create context for singleton conditional on boolean list property`() {
        // given:
        val properties = TestApplicationProperties(
            "property" to BooleanListProperty(listOf(true, false))
        )

        // and:
        val componentA1 = ComponentA1()
        val componentA2 = ComponentA2()

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java),
            dependencies = emptyList(),
            conditions = listOf(
                SingletonDefinition.Condition.OnProperty(
                    property = "property",
                    havingValue = "[true, false]"
                )
            ),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA2::class.java),
            dependencies = emptyList(),
            conditions = listOf(
                SingletonDefinition.Condition.OnProperty(
                    property = "property",
                    havingValue = "[false, true]"
                )
            ),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { properties }
        ).create(config { enabled = true })

        // then:
        assertThat(context.size()).isEqualTo(1)
        assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
    }

    private companion object {

        inline fun <reified T : Any> identifier(type: Class<T>): SingletonDefinition.Identifier<T> =
            SingletonDefinition.Identifier(typeRef = object : TypeReference<T>() {})

        inline fun <reified T : Any> identifier(typeRef: TypeReference<T>): SingletonDefinition.Identifier<T> =
            SingletonDefinition.Identifier(typeRef = typeRef)

        inline fun <reified T : Any> singletonDependency(type: Class<T>): SingletonDefinition.DependencyIdentifier<T> =
            SingletonDefinition.DependencyIdentifier.Singleton(typeRef = object : TypeReference<T>() {})

        inline fun <reified T : Any> singletonDependency(typeRef: TypeReference<T>): SingletonDefinition.DependencyIdentifier<T> =
            SingletonDefinition.DependencyIdentifier.Singleton(typeRef = typeRef)

        inline fun <reified T : Any> propertyDependency(
            key: String,
            noinline valueProvider: (Property) -> T,
            required: Boolean
        ): SingletonDefinition.DependencyIdentifier<T> =
            SingletonDefinition.DependencyIdentifier.Property(
                key = key,
                valueProvider = valueProvider,
                required = required
            )
    }

}

private fun config(init: JavalinXtConfiguration.Context.() -> Unit = {}): JavalinXtConfiguration.Context =
    JavalinXtConfiguration.Context().apply(init)

// general purpose types for testing
private interface TypeA
private interface TypeB

// general purpose classes for testing
// @formatter:off
private open class ComponentA(vararg components: Pair<String, Any?>) : TypeA { val components = mapOf(*components) }
private open class ComponentB(vararg components: Pair<String, Any?>) : TypeB { val components = mapOf(*components) }
private open class ComponentC(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
private open class ComponentD(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
private open class ComponentE(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
private open class ComponentG<T>(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
// @formatter:on

private class ComponentA1(vararg components: Pair<String, Any?>) : ComponentA(*components)
private class ComponentA2(vararg components: Pair<String, Any?>) : ComponentA(*components)

private class ComponentB1(vararg components: Pair<String, Any?>) : ComponentB(*components)
private class ComponentB2(vararg components: Pair<String, Any?>) : ComponentB(*components)

private class TestApplicationProperties(vararg properties: Pair<String, Property>) : ApplicationProperties {

    private val properties = mapOf(*properties)

    override fun get(key: String): Property = properties[key] ?: throw NoSuchElementException("Property not found")

    override fun getOrNull(key: String): Property? = properties[key]
}