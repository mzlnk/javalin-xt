package io.mzlnk.javalin.xt.context

import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.context.internal.management.ApplicationContextFactory
import io.mzlnk.javalin.xt.properties.Property
import io.mzlnk.javalin.xt.properties.internal.management.*
import io.mzlnk.javalin.xt.properties.internal.management.BooleanListProperty
import io.mzlnk.javalin.xt.properties.internal.management.BooleanProperty
import io.mzlnk.javalin.xt.properties.internal.management.NumberListProperty
import io.mzlnk.javalin.xt.properties.internal.management.NumberProperty
import io.mzlnk.javalin.xt.properties.internal.management.StringListProperty
import io.mzlnk.javalin.xt.properties.internal.management.StringProperty
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class `ApplicationContextTest_application-properties` {

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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo("value")
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(42)
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(42L)
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(42.0f)
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(42.0)
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(true)
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(listOf("value1", "value2"))
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(listOf(42, 43))
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(listOf(42L, 43L))
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(listOf(42.0f, 43.0f))
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(listOf(42.0, 43.0))
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
        ).create()

        // then:
        val component = context.findInstance(ComponentA::class.java) ?: Assertions.fail("Component A not found")
        Assertions.assertThat(component.components["property"]).isEqualTo(listOf(true, false))
    }

}