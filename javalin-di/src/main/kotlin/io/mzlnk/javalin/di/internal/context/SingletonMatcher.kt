package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.type.TypeReference

internal interface SingletonMatcher {

    fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean

    companion object {

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

private class SingularSingletonMatcher(
    identifier: SingletonDefinition.Identifier<*>
) : SingletonMatcher {

    private val typeRef = identifier.typeRef

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return this.typeRef.isAssignableFrom(identifier.typeRef)
    }
}

private class ListSingletonMatcher(
    identifier: SingletonDefinition.Identifier<List<Any>>
) : SingletonMatcher {

    private val listTypeRef = identifier.typeRef
    private val elementTypeRef = identifier.typeRef.elementType

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return this.listTypeRef == identifier.typeRef || elementTypeRef.isAssignableFrom(identifier.typeRef)
    }
}
