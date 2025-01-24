package io.mzlnk.javalin.xt.context.internal.management

import io.mzlnk.javalin.xt.context.TypeReference
import io.mzlnk.javalin.xt.context.generated.SingletonDefinition

/**
 * Consists of requirements that are used to create a matcher for singleton definitions.
 *
 * There are two types of matchers and their requirements:
 * - Singular - used for regular singleton definitions. See: [Singular]
 * - List - used for singleton definitions of list types that are treated in different way. See: [List]
 */
internal sealed interface SingletonToMatch<T : Any> {

    /**
     * Represents requirements for matcher of regular singleton definitions.
     *
     * @param typeRef type reference of the singleton
     * @param name name of the singleton
     */
    data class Singular<T : Any>(
        val typeRef: TypeReference<T>,
        val name: String? = null
    ) : SingletonToMatch<T> {

        override fun toString(): String = "${typeRef.type.typeName}${name?.let { " ($it)" } ?: ""}"
    }

    /**
     * Represents requirements for matcher of singleton definitions of list types.
     *
     * @param typeRef type reference of the list singleton
     * @param name name of the list singleton
     * @param elementName name of the elements of the list singleton
     */
    data class List<T : Any>(
        val typeRef: TypeReference<out kotlin.collections.List<T>>,
        val name: String? = null,
        val elementName: String? = null
    ) : SingletonToMatch<kotlin.collections.List<T>> {

        override fun toString(): String = "${typeRef.type.typeName}${name?.let { " ($it)" } ?: ""}"

    }

}

/**
 * Represents a matcher for singleton definitions. It is used to both find a singleton instance in the context
 * and create a dependency graph during context creation - based on the singleton identifier.
 *
 * Matcher is created based on requirements that candidate singleton must meet.
 *
 * There are two types of matchers:
 * - singular - used for regular singleton definitions. See: [matchesSingular]
 * - list     - used for singleton definitions of list types that are treated in different way
 *              as they can be match with both list types and their element types to e.g. allow
 *              defining dependencies as list of all available components of given type.
 *              See: [matchesList]
 *
 */
internal object SingletonMatcher {

    fun matches(toMatch: SingletonToMatch<*>, candidate: SingletonDefinition.Identifier<*>): Boolean =
        when (toMatch) {
            is SingletonToMatch.Singular<*> -> matchesSingular(toMatch, candidate)
            is SingletonToMatch.List<*> -> matchesList(toMatch, candidate)
        }

    /**
     * Matcher used for regular singleton definitions.
     *
     * Matching matrix:
     * matches(A, B) = A.matchesTypeRef(B) && A.matchesName(B)
     *
     * A.matchesTypeRef(B) matrix:
     * +-------------------------------+---------------------+
     * | relation between A and B      | A.matchesTypeRef(B) |
     * +-------------------------------+---------------------+
     * | A is the same type as B       | true                |
     * | A is a super type of B        | true                |
     * | A implements B                | true                |
     * | A is different type than B    | false               |
     * +-------------------------------+---------------------+
     *
     * A.matchesName(B) matrix:
     * +--------+--------+------------------+
     * | A name | B name | A.matchesName(B) |
     * +--------+--------+------------------+
     * | -      | -      | true             |
     * | N      | -      | false            |
     * | -      | N      | true             |
     * | N      | N      | true             |
     * | N      | M      | false            |
     * +--------+--------+------------------+
     */
    private fun matchesSingular(
        toMatch: SingletonToMatch.Singular<*>,
        candidate: SingletonDefinition.Identifier<*>
    ): Boolean {
        val matchesTypeRef: Boolean = toMatch.typeRef.isAssignableFrom(candidate.typeRef)
        val matchesName: Boolean = toMatch.name?.let { it == candidate.name } ?: true

        return matchesTypeRef && matchesName
    }

    /**
     * Matcher used for singleton definitions of list types.
     *
     * List types are treated in different way as they can be match with both list types and their element types
     * to e.g. allow defining dependencies as list of all available components of given type.
     *
     * Matching matrix:
     * matches(A, B) = A.matchesTypeRef(B) && A.matchesName(B)
     *
     * A.matchesTypeRef(B) matrix:
     * +--------------------------------------------------------------+---------------------+
     * | relation between A and B                                     | A.matchesTypeRef(B) |
     * +--------------------------------------------------------------+---------------------+
     * | A is List<X> and B is List<X>                                | true                |
     * | A is List<X> and B is X                                      | true                |
     * | A is List<X> and B is List<Y>                                | false               |
     * | A is List<X> and B is Y                                      | false               |
     * | A is List<X> and B is Y where X is super type of Y           | true                |
     * | A is List<X> and B is Y where Y implements X                 | true                |
     * | A is List<X> and B is List<Y> where X is super type of Y     | false               |
     * | A is List<X> and B is List<Y> where Y implements X           | false               |
     * +--------------------------------------------------------------+---------------------+
     *
     * A.matchesName(B) matrix:
     *
     * I: A is List<X> and B is List<X>
     * +---------------+------------+------------+------------------+
     * | A elementName | A name     | B name     | A.matchesName(B) |
     * +---------------+------------+------------+------------------+
     * | N             | <anything> | <anything> | true             |
     * | -             | -          | -          | true             |
     * | -             | N          | -          | false            |
     * | -             | -          | N          | true             |
     * | -             | N          | N          | true             |
     * | -             | N          | M          | false            |
     * +---------------+------------+------------+------------------+
     *
     * II: A is List<X> and B is X
     * +--------+---------------+------------+------------------+
     * | A.name | A.elementName | B.name     | A.matchesName(B) |
     * +--------+---------------+------------+------------------+
     * | N      | <anything>    | <anything> | false            |
     * | -      | -             | -          | true             |
     * | -      | N             | -          | false            |
     * | -      | -             | N          | true             |
     * | -      | N             | N          | true             |
     * | -      | N             | M          | false            |
     * +--------+---------------+------------+------------------+
     */
    private fun matchesList(
        toMatch: SingletonToMatch.List<*>,
        candidate: SingletonDefinition.Identifier<*>
    ): Boolean {
        return if (candidate.typeRef.isList()) {
            toMatch.typeRef == candidate.typeRef
                    && toMatch.elementName?.let { false } ?: toMatch.name?.let { it == candidate.name } ?: true
        } else {
            toMatch.typeRef.elementType.isAssignableFrom(candidate.typeRef)
                    && toMatch.name?.let { false } ?: toMatch.elementName?.let { it == candidate.name } ?: true
        }
    }

}