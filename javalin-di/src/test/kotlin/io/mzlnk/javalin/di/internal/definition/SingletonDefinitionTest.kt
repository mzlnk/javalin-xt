package io.mzlnk.javalin.di.internal.definition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SingletonDefinitionTest {

    @Test
    fun `should return string representation for singleton definition key with name`() {
        // given:
        val type = Type(
            packageName = TestClass::class.java.packageName,
            name = TestClass::class.java.simpleName
        )
        val name = "testName"

        // and:
        val key = SingletonDefinition.Key(
            type = type,
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
        val type = Type(
            packageName = TestClass::class.java.packageName,
            name = TestClass::class.java.simpleName
        )

        // and:
        val key = SingletonDefinition.Key(
            type = type,
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
        val clazz = Clazz(
            type = Type(
                packageName = TestClass::class.java.packageName,
                name = TestClass::class.java.simpleName
            )
        )

        val method =Method(
            returnType = Type(
                packageName = Unit::class.java.packageName,
                name = Unit::class.java.simpleName
            ),
            name = "testMethod")

        // and:
        val source = SingletonDefinition.Source(
            clazz = clazz,
            method = method
        )

        // when:
        val result = source.toString()

        // then:
        assertThat(result).isEqualTo("${TestClass::class.java.canonicalName}#${method.name}")

    }

}

private class TestClass {

    fun testMethod() {}

}