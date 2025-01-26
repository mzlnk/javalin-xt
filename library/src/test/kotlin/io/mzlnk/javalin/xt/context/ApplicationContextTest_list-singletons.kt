package io.mzlnk.javalin.xt.context

import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.context.internal.management.ApplicationContextFactory
import io.mzlnk.javalin.xt.properties.internal.management.EmptyApplicationProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class `ApplicationContextTest_list-singletons` {

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
        ).create()

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
        ).create()

        // then:
        val components = context.findInstance(object : TypeReference<List<ComponentA>>() {})
        assertThat(components).containsExactlyInAnyOrder(componentA1, componentA2)
    }
    
}