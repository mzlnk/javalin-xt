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
 * - SingularSingletonMatcher - used for regular singleton definitions. See: [SingularSingletonMatcher]
 * - ListSingletonMatcher - used for singleton definitions of list types that are treated in different way
 *                          as they can be match with both list types and their element types to e.g. allow
 *                          defining dependencies as list of all available components of given type.
 *                          See: [ListSingletonMatcher]
 *
 *
 */
internal interface SingletonMatcher {

    fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean

    companion object {

        /**
         * Creates a matcher for singleton with given identifier.
         *
         * @param identifier identifier of the singleton
         *
         * @return matcher for the singleton
         */
        fun matcherFor(singletonToMatch: SingletonToMatch<*>): SingletonMatcher = when (singletonToMatch) {
            is SingletonToMatch.Singular<*> -> SingularSingletonMatcher(
                typeRef = singletonToMatch.typeRef,
                name = singletonToMatch.name
            )

            is SingletonToMatch.List<*> -> ListSingletonMatcher(
                typeRef = singletonToMatch.typeRef,
                name = singletonToMatch.name,
                elementName = singletonToMatch.elementName
            )
        }

    }

}

/**
 * Matcher used for regular singleton definitions.
 *
 * Matching matrix:
 * A.matches(B) = A.matchesTypeRef(B) && A.matchesName(B)
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
private class SingularSingletonMatcher(
    private val typeRef: TypeReference<*>,
    private val name: String?
) : SingletonMatcher {

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean =
        matchesTypeRef(identifier.typeRef) && matchesName(identifier.name)

    private fun matchesTypeRef(typeRef: TypeReference<*>): Boolean = this.typeRef.isAssignableFrom(typeRef)
    private fun matchesName(name: String?): Boolean = this.name?.let { it == name } ?: true
}

/**
 * Matcher used for singleton definitions of list types.
 *
 * List types are treated in different way as they can be match with both list types and their element types
 * to e.g. allow defining dependencies as list of all available components of given type.
 *
 * Matching matrix:
 * A.matches(B) = A.matchesTypeRef(B) && A.matchesName(B)
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
private class ListSingletonMatcher(
    private val typeRef: TypeReference<out List<Any>>,
    private val name: String?,
    private val elementName: String?
) : SingletonMatcher {

    private val elementTypeRef: TypeReference<*> = typeRef.elementType

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return if (identifier.typeRef.isList()) {
            this.typeRef == identifier.typeRef
                    && this.elementName?.let { false } ?: this.name?.let { it == identifier.name } ?: true
        } else {
            this.elementTypeRef.isAssignableFrom(identifier.typeRef)
                    && this.name?.let { false } ?: this.elementName?.let { it == identifier.name } ?: true
        }
    }

}
