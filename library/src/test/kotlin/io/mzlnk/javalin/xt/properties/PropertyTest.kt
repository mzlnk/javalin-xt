package io.mzlnk.javalin.xt.properties

import io.mzlnk.javalin.xt.properties.internal.management.*
import io.mzlnk.javalin.xt.properties.internal.management.BooleanListProperty
import io.mzlnk.javalin.xt.properties.internal.management.BooleanProperty
import io.mzlnk.javalin.xt.properties.internal.management.NumberListProperty
import io.mzlnk.javalin.xt.properties.internal.management.NumberProperty
import io.mzlnk.javalin.xt.properties.internal.management.StringListProperty
import io.mzlnk.javalin.xt.properties.internal.management.StringProperty
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PropertyTest {

    @Test
    fun `should get only numeric values from number property`() {
        // given:
        val property = NumberProperty(10)

        // expect:
        Assertions.assertThatCode { property.asLong }.doesNotThrowAnyException()
        Assertions.assertThatCode { property.asInt }.doesNotThrowAnyException()
        Assertions.assertThatCode { property.asDouble }.doesNotThrowAnyException()
        Assertions.assertThatCode { property.asFloat }.doesNotThrowAnyException()

        // and:
        Assertions.assertThatThrownBy { property.asString }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string")
        Assertions.assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        Assertions.assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        Assertions.assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
        Assertions.assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
    }

    @Test
    fun `should get only string value from string property`() {
        // given:
        val property = StringProperty("test")

        // expect:
        Assertions.assertThatCode { property.asString }.doesNotThrowAnyException()

        // and:
        Assertions.assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        Assertions.assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        Assertions.assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        Assertions.assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        Assertions.assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        Assertions.assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        Assertions.assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
        Assertions.assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
    }

    @Test
    fun `should get only boolean value from boolean property`() {
        // given:
        val property = BooleanProperty(true)

        // expect:
        Assertions.assertThatCode { property.asBoolean }.doesNotThrowAnyException()

        // and:
        Assertions.assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        Assertions.assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        Assertions.assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        Assertions.assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        Assertions.assertThatThrownBy { property.asString }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string")

        // and:
        Assertions.assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        Assertions.assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
        Assertions.assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
    }

    @Test
    fun `should get only numeric lists from number list property`() {
        // given:
        val property = NumberListProperty(listOf(1, 2, 3))

        // expect:
        Assertions.assertThatCode { property.asIntList }.doesNotThrowAnyException()
        Assertions.assertThatCode { property.asLongList }.doesNotThrowAnyException()
        Assertions.assertThatCode { property.asDoubleList }.doesNotThrowAnyException()
        Assertions.assertThatCode { property.asFloatList }.doesNotThrowAnyException()

        // and:
        Assertions.assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        Assertions.assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        Assertions.assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        Assertions.assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        Assertions.assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        Assertions.assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        Assertions.assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
    }

    @Test
    fun `should get only string list from string list property`() {
        // given:
        val property = StringListProperty(listOf("test", "test2"))

        // expect:
        Assertions.assertThatCode { property.asStringList }.doesNotThrowAnyException()

        // and:
        Assertions.assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        Assertions.assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        Assertions.assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        Assertions.assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        Assertions.assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        Assertions.assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
    }

    @Test
    fun `should get only boolean list from boolean list property`() {
        // given:
        val property = BooleanListProperty(listOf(true, false))

        // expect:
        Assertions.assertThatCode { property.asBooleanList }.doesNotThrowAnyException()

        // and:
        Assertions.assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        Assertions.assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        Assertions.assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        Assertions.assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        Assertions.assertThatThrownBy { property.asString }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string")

        // and:
        Assertions.assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        Assertions.assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
    }

    @Test
    fun `should cut off decimal part of float value in number property when get as integer value`() {
        // given:
        val property = NumberProperty(10.5)

        // expect:
        Assertions.assertThat(property.asInt).isEqualTo(10)
    }

    @Test
    fun `should cut off decimal part of every float value in number list property when get as integer list value`() {
        // given:
        val property = NumberListProperty(listOf(10.5, 20.5, 30.5))

        // expect:
        Assertions.assertThat(property.asIntList).containsExactly(10, 20, 30)
    }

    @Test
    fun `should get string representation of number property`() {
        // given:
        val property = NumberProperty(10)

        // expect:
        Assertions.assertThat(property.toString()).isEqualTo("10")
    }

    @Test
    fun `should get string representation of string property`() {
        // given:
        val property = StringProperty("test")

        // expect:
        Assertions.assertThat(property.toString()).isEqualTo("test")
    }

    @Test
    fun `should get string representation of boolean property`() {
        // given:
        val property = BooleanProperty(true)

        // expect:
        Assertions.assertThat(property.toString()).isEqualTo("true")
    }

    @Test
    fun `should get string representation of number list property`() {
        // given:
        val property = NumberListProperty(listOf(1, 2, 3))

        // expect:
        Assertions.assertThat(property.toString()).isEqualTo("[1, 2, 3]")
    }

    @Test
    fun `should get string representation of string list property`() {
        // given:
        val property = StringListProperty(listOf("test", "test2"))

        // expect:
        Assertions.assertThat(property.toString()).isEqualTo("[test, test2]")
    }

    @Test
    fun `should get string representation of boolean list property`() {
        // given:
        val property = BooleanListProperty(listOf(true, false))

        // expect:
        Assertions.assertThat(property.toString()).isEqualTo("[true, false]")
    }

}