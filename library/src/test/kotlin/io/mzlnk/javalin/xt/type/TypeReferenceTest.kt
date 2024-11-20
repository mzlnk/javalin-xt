package io.mzlnk.javalin.xt.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TypeReferenceTest {

    @Test
    fun `should return type for simple type`() {
        // given:
        val typeReference = object : TypeReference<String>() {}

        // expect:
        assertThat(typeReference.type.typeName).isEqualTo("java.lang.String")
    }

    @Test
    fun `should return type for generic type`() {
        // given:
        val typeReference = object : TypeReference<List<String>>() {}

        // expect:
        assertThat(typeReference.type.typeName).isEqualTo("java.util.List<? extends java.lang.String>")
    }

    @Test
    fun `should return type for nested generic type`() {
        // given:
        val typeReference = object : TypeReference<Map<String, List<Int>>>() {}

        // expect:
        assertThat(typeReference.type.typeName).isEqualTo("java.util.Map<java.lang.String, ? extends java.util.List<? extends java.lang.Integer>>")
    }

}