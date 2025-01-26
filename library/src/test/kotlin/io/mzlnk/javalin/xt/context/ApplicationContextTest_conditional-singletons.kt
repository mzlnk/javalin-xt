package io.mzlnk.javalin.xt.context

import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.context.internal.management.ApplicationContextFactory
import io.mzlnk.javalin.xt.properties.internal.management.BooleanListProperty
import io.mzlnk.javalin.xt.properties.internal.management.BooleanProperty
import io.mzlnk.javalin.xt.properties.internal.management.NumberListProperty
import io.mzlnk.javalin.xt.properties.internal.management.NumberProperty
import io.mzlnk.javalin.xt.properties.internal.management.StringListProperty
import io.mzlnk.javalin.xt.properties.internal.management.StringProperty
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class `ApplicationContextTest_conditional-singletons` {

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
        ).create()

        // then:
        Assertions.assertThat(context.size()).isEqualTo(1)
        Assertions.assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
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
        ).create()

        // then:
        Assertions.assertThat(context.size()).isEqualTo(1)
        Assertions.assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
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
        ).create()

        // then:
        Assertions.assertThat(context.size()).isEqualTo(1)
        Assertions.assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
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
        ).create()

        // then:
        Assertions.assertThat(context.size()).isEqualTo(1)
        Assertions.assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
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
        ).create()

        // then:
        Assertions.assertThat(context.size()).isEqualTo(1)
        Assertions.assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
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
        ).create()

        // then:
        Assertions.assertThat(context.size()).isEqualTo(1)
        Assertions.assertThat(context.findInstance(ComponentA::class.java)).isEqualTo(componentA1)
    }

}