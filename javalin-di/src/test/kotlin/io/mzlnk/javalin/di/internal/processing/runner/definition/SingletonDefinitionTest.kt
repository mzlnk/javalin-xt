package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.internal.processing.Clazz
import io.mzlnk.javalin.di.internal.processing.Method
import io.mzlnk.javalin.di.internal.processing.runner.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.processing.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SingletonDefinitionTest {

    @Test
    fun `should return string representation for singleton definition key with name`() {
        // given:
        val type = Type(
            packageName = "io.mzlnk.javalin.di.test",
            name = "TestClass"
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
        assertThat(result).isEqualTo("io.mzlnk.javalin.di.test.TestClass(testName)")
    }

    @Test
    fun `should return string representation for singleton definition key without name`() {
        // given:
        val type = Type(
            packageName = "io.mzlnk.javalin.di.test",
            name = "TestClass"
        )

        // and:
        val key = SingletonDefinition.Key(
            type = type,
            name = null
        )

        // when:
        val result = key.toString()

        // then:
        assertThat(result).isEqualTo("io.mzlnk.javalin.di.test.TestClass")
    }

    @Test
    fun `should return string representation for singleton definition source`() {
        // given:
        val clazz = Clazz(
            type = Type(
                packageName = "io.mzlnk.javalin.di.test",
                name = "TestClass"
            )
        )

        val method = Method(
            returnType = Type(
                packageName = "kotlin",
                name = "Unit"
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
        assertThat(result).isEqualTo("io.mzlnk.javalin.di.test.TestClass#testMethod")

    }

}