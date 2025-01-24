package io.mzlnk.javalin.xt.context

import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.context.internal.management.ApplicationContextFactory
import io.mzlnk.javalin.xt.properties.internal.management.EmptyApplicationProperties
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class `ApplicationContextTest_named-singletons` {

    @Test
    fun `should get named singleton`() {
        // given:
        val componentA1 = ComponentA("name" to "A1")
        val componentA2 = ComponentA("name" to "A2")

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java, name = "A1"),
            dependencies = emptyList(),
            instanceProvider = { componentA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java, name = "A2"),
            dependencies = emptyList(),
            instanceProvider = { componentA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create()

        // then:
        Assertions.assertThat(context.findInstance(ComponentA::class.java, name = "A1")).isEqualTo(componentA1)
        Assertions.assertThat(context.findInstance(ComponentA::class.java, name = "A2")).isEqualTo(componentA2)
    }

    @Test
    fun `should get list of named singletons`() {
        // given:
        val componentAA1 = ComponentA()
        val componentAA2 = ComponentA()
        val componentAB1 = ComponentA()

        // and:
        val singletonAA1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java, name = "A"),
            dependencies = emptyList(),
            instanceProvider = { componentAA1 }
        )

        val singletonAA2 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java, name = "A"),
            dependencies = emptyList(),
            instanceProvider = { componentAA2 }
        )

        val singletonAB1 = SingletonDefinition(
            identifier = identifier(ComponentA::class.java, name = "B"),
            dependencies = emptyList(),
            instanceProvider = { componentAB1 }
        )

        // and:
        val definitions = listOf(singletonAA1, singletonAA2, singletonAB1)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create()

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {}, elementName = "A")
            ?: Assertions.fail("Components not found")

        Assertions.assertThat(components).containsExactlyInAnyOrder(componentAA1, componentAA2)
    }

    @Test
    fun `should get named list of singletons`() {
        // given:
        val componentsA1 = listOf(ComponentA(), ComponentA())
        val componentsA2 = listOf(ComponentA(), ComponentA())

        // and:
        val singletonA1 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}, name = "A1"),
            dependencies = emptyList(),
            instanceProvider = { componentsA1 }
        )

        val singletonA2 = SingletonDefinition(
            identifier = identifier(object : TypeReference<List<ComponentA>>() {}, name = "A2"),
            dependencies = emptyList(),
            instanceProvider = { componentsA2 }
        )

        // and:
        val definitions = listOf(singletonA1, singletonA2)

        // when:
        val context = ApplicationContextFactory(
            definitionSource = { definitions },
            propertiesSource = { EmptyApplicationProperties }
        ).create()

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {}, name = "A1")
            ?: Assertions.fail("Components not found")

        Assertions.assertThat(components).containsExactlyInAnyOrder(*componentsA1.toTypedArray())
    }

}