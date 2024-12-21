package io.mzlnk.javalin.xt.internal.properties

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

class PropertyTest {

    @Test
    fun `should get only numeric values from number property`() {
        // given:
        val property = NumberProperty(10)

        // expect:
        assertThatCode { property.asLong }.doesNotThrowAnyException()
        assertThatCode { property.asInt }.doesNotThrowAnyException()
        assertThatCode { property.asDouble }.doesNotThrowAnyException()
        assertThatCode { property.asFloat }.doesNotThrowAnyException()

        // and:
        assertThatThrownBy { property.asString }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string")
        assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
        assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
    }

    @Test
    fun `should get only string value from string property`() {
        // given:
        val property = StringProperty("test")

        // expect:
        assertThatCode { property.asString }.doesNotThrowAnyException()

        // and:
        assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
        assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
    }

    @Test
    fun `should get only boolean value from boolean property`() {
        // given:
        val property = BooleanProperty(true)

        // expect:
        assertThatCode { property.asBoolean }.doesNotThrowAnyException()

        // and:
        assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        assertThatThrownBy { property.asString }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string")

        // and:
        assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
        assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
    }

    @Test
    fun `should get only numeric lists from number list property`() {
        // given:
        val property = NumberListProperty(listOf(1, 2, 3))

        // expect:
        assertThatCode { property.asIntList }.doesNotThrowAnyException()
        assertThatCode { property.asLongList }.doesNotThrowAnyException()
        assertThatCode { property.asDoubleList }.doesNotThrowAnyException()
        assertThatCode { property.asFloatList }.doesNotThrowAnyException()

        // and:
        assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
        assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
    }

    @Test
    fun `should get only string list from string list property`() {
        // given:
        val property = StringListProperty(listOf("test", "test2"))

        // expect:
        assertThatCode { property.asStringList }.doesNotThrowAnyException()

        // and:
        assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        assertThatThrownBy { property.asBoolean }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean")

        // and:
        assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asBooleanList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a boolean list")
    }

    @Test
    fun `should get only boolean list from boolean list property`() {
        // given:
        val property = BooleanListProperty(listOf(true, false))

        // expect:
        assertThatCode { property.asBooleanList }.doesNotThrowAnyException()

        // and:
        assertThatThrownBy { property.asLong }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a long")
        assertThatThrownBy { property.asInt }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not an int")
        assertThatThrownBy { property.asDouble }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a double")
        assertThatThrownBy { property.asFloat }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a float")
        assertThatThrownBy { property.asString }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string")

        // and:
        assertThatThrownBy { property.asIntList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asLongList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asDoubleList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asFloatList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a number list")
        assertThatThrownBy { property.asStringList }.isInstanceOf(IllegalStateException::class.java).hasMessage("Property is not a string list")
    }

    @Test
    fun `should cut off decimal part of float value in number property when get as integer value`() {
        // given:
        val property = NumberProperty(10.5)

        // expect:
        assertThat(property.asInt).isEqualTo(10)
    }

    @Test
    fun `should cut off decimal part of every float value in number list property when get as integer list value`() {
        // given:
        val property = NumberListProperty(listOf(10.5, 20.5, 30.5))

        // expect:
        assertThat(property.asIntList).containsExactly(10, 20, 30)
    }

    @Test
    fun `should get string representation of number property`() {
        // given:
        val property = NumberProperty(10)

        // expect:
        assertThat(property.toString()).isEqualTo("10")
    }

    @Test
    fun `should get string representation of string property`() {
        // given:
        val property = StringProperty("test")

        // expect:
        assertThat(property.toString()).isEqualTo("test")
    }

    @Test
    fun `should get string representation of boolean property`() {
        // given:
        val property = BooleanProperty(true)

        // expect:
        assertThat(property.toString()).isEqualTo("true")
    }

    @Test
    fun `should get string representation of number list property`() {
        // given:
        val property = NumberListProperty(listOf(1, 2, 3))

        // expect:
        assertThat(property.toString()).isEqualTo("[1, 2, 3]")
    }

    @Test
    fun `should get string representation of string list property`() {
        // given:
        val property = StringListProperty(listOf("test", "test2"))

        // expect:
        assertThat(property.toString()).isEqualTo("[test, test2]")
    }

    @Test
    fun `should get string representation of boolean list property`() {
        // given:
        val property = BooleanListProperty(listOf(true, false))

        // expect:
        assertThat(property.toString()).isEqualTo("[true, false]")
    }

}