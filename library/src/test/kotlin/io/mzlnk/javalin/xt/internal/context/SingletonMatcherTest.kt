package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.context.TypeReference
import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
import io.mzlnk.javalin.xt.utils.testCase
import io.mzlnk.javalin.xt.utils.testCases
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SingletonMatcherTest {

    @ParameterizedTest
    @MethodSource("params for return match result for matcher of singular singleton")
    fun `should return match result for matcher of singular singleton`(
        matcherTypeRef: TypeReference<Any>,
        matcherName: String?,
        toMatchTypeRef: TypeReference<Any>,
        toMatchName: String?,
        expectedResult: Boolean
    ) {
        // given:
        val matcher = SingletonMatcher.matcherFor(
            singletonToMatch = SingletonToMatch.Singular(matcherTypeRef, matcherName)
        )

        // when:
        val result = matcher.matches(
            identifier = SingletonDefinition.Identifier(
                typeRef = toMatchTypeRef,
                name = toMatchName
            )
        )

        // then:
        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("params for return match result for matcher of list singleton")
    fun `should return match result for matcher of list singleton`(
        matcherTypeRef: TypeReference<out List<Any>>,
        matcherName: String?,
        matcherElementName: String?,
        toMatchTypeRef: TypeReference<Any>,
        toMatchName: String?,
        expectedResult: Boolean
    ) {
        // given:
        val matcher = SingletonMatcher.matcherFor(
            singletonToMatch = SingletonToMatch.List(matcherTypeRef, matcherName, matcherElementName)
        )

        // when:
        val result = matcher.matches(
            identifier = SingletonDefinition.Identifier(
                typeRef = toMatchTypeRef,
                name = toMatchName
            )
        )

        // then:
        assertThat(result).isEqualTo(expectedResult)
    }

    private companion object {

        @JvmStatic
        fun `params for return match result for matcher of singular singleton`(): Stream<Arguments> = testCases(
            // @formatter:off
            //      | matcher.typeRef  | matcher.name | toMatch.typeRef  | toMatch.name | expectedResult |
            testCase( T<ComponentA1>() , null         , T<ComponentA1>() , null         , true           ),
            testCase( T<ComponentA>()  , null         , T<ComponentA1>() , null         , true           ),
            testCase( T<ComponentA1>() , null         , T<ComponentA>()  , null         , false          ),
            testCase( T<TypeA>()       , null         , T<ComponentA1>() , null         , true           ),
            testCase( T<ComponentA1>() , null         , T<TypeA>()       , null         , false          ),
            testCase( T<ComponentA>()  , null         , T<ComponentB>()  , null         , false          ),
            testCase( T<ComponentA>()  , "X"          , T<ComponentA>()  , null         , false          ),
            testCase( T<ComponentA>()  , null         , T<ComponentA>()  , "X"          , true           ),
            testCase( T<ComponentA>()  , "X"          , T<ComponentA>()  , "X"          , true           ),
            testCase( T<ComponentA>()  , "X"          , T<ComponentA>()  , "Y"          , false          ),
            // @formatter:on
        )

        @JvmStatic
        fun `params for return match result for matcher of list singleton`(): Stream<Arguments> = testCases(
            // @formatter:off
            //      | matcher.typeRef        | matcher.name | matcher.elementName | toMatch.typeRef        | toMatch.name | expectedResult |
            testCase( T<List<ComponentA1>>() , null         , null                , T<List<ComponentA1>>() , null         , true           ),
            testCase( T<List<ComponentA1>>() , null         , null                , T<ComponentA1>()       , null         , true           ),
            testCase( T<List<ComponentA>>()  , null         , null                , T<List<ComponentB>>()  , null         , false          ),
            testCase( T<List<ComponentA>>()  , null         , null                , T<ComponentB>()        , null         , false          ),
            testCase( T<List<ComponentA>>()  , null         , null                , T<ComponentA1>()       , null         , true           ),
            testCase( T<List<TypeA>>()       , null         , null                , T<ComponentA1>()       , null         , true           ),
            testCase( T<List<ComponentA>>()  , null         , null                , T<List<ComponentA1>>() , null         , false          ),
            testCase( T<List<TypeA>>()       , null         , null                , T<List<ComponentA1>>() , null         , false          ),
            // A.matchesName(B) matrix: I: A is List<X> and B is List<X>:
            testCase( T<List<ComponentA1>>() , null         , "X"                 , T<List<ComponentA1>>() , null         , false          ),
            testCase( T<List<ComponentA1>>() , "Y"          , "X"                 , T<List<ComponentA1>>() , null         , false          ),
            testCase( T<List<ComponentA1>>() , null         , "X"                 , T<List<ComponentA1>>() , "Y"          , false          ),
            testCase( T<List<ComponentA1>>() , "Y"          , "X"                 , T<List<ComponentA1>>() , "Y"          , false          ),
            testCase( T<List<ComponentA1>>() , "Y"          , "X"                 , T<List<ComponentA1>>() , "Z"          , false          ),
            testCase( T<List<ComponentA1>>() , "Y"          , null                , T<List<ComponentA1>>() , null         , false          ),
            testCase( T<List<ComponentA1>>() , null         , null                , T<List<ComponentA1>>() , "Y"          , true           ),
            testCase( T<List<ComponentA1>>() , "Y"          , null                , T<List<ComponentA1>>() , "Y"          , true           ),
            testCase( T<List<ComponentA1>>() , "Y"          , null                , T<List<ComponentA1>>() , "Z"          , false          ),
            // A.matchesName(B) matrix: II: A is List<X> and B is X:
            testCase( T<List<ComponentA1>>() , "X"          , null                , T<ComponentA1>()       , null         , false          ),
            testCase( T<List<ComponentA1>>() , "X"          , "Y"                 , T<ComponentA1>()       , null         , false          ),
            testCase( T<List<ComponentA1>>() , "X"          , null                , T<ComponentA1>()       , "Y"          , false          ),
            testCase( T<List<ComponentA1>>() , "X"          , "Y"                 , T<ComponentA1>()       , "Y"          , false          ),
            testCase( T<List<ComponentA1>>() , "X"          , "Y"                 , T<ComponentA1>()       , "Z"          , false          ),
            testCase( T<List<ComponentA1>>() , null         , "Y"                 , T<ComponentA1>()       , null         , false          ),
            testCase( T<List<ComponentA1>>() , null         , null                , T<ComponentA1>()       , "Y"          , true           ),
            testCase( T<List<ComponentA1>>() , null         , "Y"                 , T<ComponentA1>()       , "Y"          , true           ),
            testCase( T<List<ComponentA1>>() , null         , "Y"                 , T<ComponentA1>()       , "Z"          , false          ),
            // @formatter:on
        )

        private inline fun <reified T : Any> T(): TypeReference<T> = object : TypeReference<T>() {}

    }


    // general purpose types for testing
    private interface TypeA
    private open class ComponentA : TypeA
    private class ComponentA1 : ComponentA()
    private class ComponentB


}
