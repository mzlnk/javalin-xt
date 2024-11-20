package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.definition.SingletonDefinition

/**
 * Represents a matcher for singleton definitions. It is used to both find a singleton instance in the context
 * and create a dependency graph during context creation - based on the singleton identifier.
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
        @Suppress("UNCHECKED_CAST")
        fun matcherFor(identifier: SingletonDefinition.Identifier<*>): SingletonMatcher {
            return if(identifier.typeRef.isList()) {
                ListSingletonMatcher(identifier as SingletonDefinition.Identifier<List<Any>>)
            } else {
                SingularSingletonMatcher(identifier)
            }
        }

    }

}

/**
 * Matcher used for regular singleton definitions.
 *
 * Matching matrix:
 * +-------------------------------+--------------+
 * | relation between A and B      | A.matches(B) |
 * +-------------------------------+--------------+
 * | A is the same type as B       | true         |
 * | A is a super type of B        | true         |
 * | A implements B                | true         |
 * | A is different type than B    | false        |
 * +-------------------------------+--------------+
 */
private class SingularSingletonMatcher(
    identifier: SingletonDefinition.Identifier<*>
) : SingletonMatcher {

    private val typeRef = identifier.typeRef

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return this.typeRef.isAssignableFrom(identifier.typeRef)
    }
}

/**
 * Matcher used for singleton definitions of list types.
 *
 * List types are treated in different way as they can be match with both list types and their element types
 * to e.g. allow defining dependencies as list of all available components of given type.
 *
 * Matching matrix:
 * +--------------------------------------------------------------+--------------+
 * | relation between A and B                                     | A.matches(B) |
 * +--------------------------------------------------------------+--------------+
 * | A is List<X> and B is List<X>                                | true         |
 * | A is List<X> and B is X                                      | true         |
 * | A is List<X> and B is List<Y>                                | false        |
 * | A is List<X> and B is Y                                      | false        |
 * | A is List<X> and B is Y where X is super type of Y           | true         |
 * | A is List<X> and B is Y where Y implements X                 | true         |
 * | A is List<X> and B is List<Y> where X is super type of Y     | false        |
 * +--------------------------------------------------------------+--------------+
 */
private class ListSingletonMatcher(
    identifier: SingletonDefinition.Identifier<List<Any>>
) : SingletonMatcher {

    private val listTypeRef = identifier.typeRef
    private val elementTypeRef = identifier.typeRef.elementType

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return this.listTypeRef == identifier.typeRef || elementTypeRef.isAssignableFrom(identifier.typeRef)
    }
}
