package io.mzlnk.javalin.xt.internal.utils.jackson

import io.mzlnk.javalin.xt.internal.utils.jackson.PathReference.Part
import io.mzlnk.javalin.xt.utils.testCase
import io.mzlnk.javalin.xt.utils.testCases
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PathReferenceTest {

    @Test
    fun `should get string representation of path reference`() {
        // given:
        val path = PathReference(parts = listOf(
            Part.Property("path1"),
            Part.Index(0),
            Part.Property("path2"),
            Part.Index(1)
        ))

        // expect:
        assertThat(path.toString()).isEqualTo("path1[0].path2[1]")
    }

    @ParameterizedTest
    @MethodSource("params for create path reference from string")
    fun `should create path reference from string`(path: String, expectedParts: List<Part>) {
        // expect:
        assertThat(PathReference.create(path)).isEqualTo(PathReference(parts = expectedParts))
    }

    @Test
    fun `should throw exception when creating path reference from invalid string`() {
        // when:
        val exception = assertThatThrownBy {
            PathReference.create("invalid.path[")
        }

        // then:
        exception.isInstanceOf(IllegalArgumentException::class.java)
        exception.hasMessage("Invalid path reference: `invalid.path[`")
    }

    private companion object {

        @JvmStatic
        fun `params for create path reference from string`(): Stream<Arguments> = testCases(
            // path | expected parts
            testCase("path1", parts("path1")),
            testCase("path1.path2", parts("path1", "path2")),
            testCase("path1[0]", parts("path1", 0)),
            testCase("path1[0].path2", parts("path1", 0, "path2")),
            testCase("path1[0].path2[1]", parts("path1", 0, "path2", 1)),
        )

    }

}

private fun parts(vararg parts: Any): List<Part> {
    return parts.map {
        when (it) {
            is String -> Part.Property(it)
            is Int -> Part.Index(it)
            else -> throw IllegalStateException()
        }
    }
}
