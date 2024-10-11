package io.mzlnk.javalin.di.core.internal.definition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SingletonDefinitionTest {

    @Test
    fun `should return string representation for singleton definition key with name`() {
        // given:
        val clazz = TestClass::class.java
        val name = "testName"

        // and:
        val key = SingletonDefinition.Key(
            type = clazz,
            name = name
        )

        // when:
        val result = key.toString()

        // then:
        assertThat(result).isEqualTo("${TestClass::class.java.canonicalName}($name)")
    }

    @Test
    fun `should return string representation for singleton definition key without name`() {
        // given:
        val clazz = TestClass::class.java

        // and:
        val key = SingletonDefinition.Key(
            type = clazz,
            name = null
        )

        // when:
        val result = key.toString()

        // then:
        assertThat(result).isEqualTo(TestClass::class.java.canonicalName)
    }

    @Test
    fun `should return string representation for singleton definition source`() {
        // given:
        val clazz = TestClass::class.java
        val method = TestClass::class.java.getDeclaredMethod("testMethod")

        // and:
        val source = SingletonDefinition.Source(
            clazz = clazz,
            method = method
        )

        // when:
        val result = source.toString()

        // then:
        assertThat(result).isEqualTo("${clazz.canonicalName}#${method.name}")

    }

}

private class TestClass {

    fun testMethod() {}

}