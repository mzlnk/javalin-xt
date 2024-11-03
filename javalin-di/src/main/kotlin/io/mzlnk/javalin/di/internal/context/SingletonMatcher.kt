package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition

internal class SingletonMatcher private constructor(
    private val identifier: SingletonDefinition.Identifier<*>
) {

    fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return this.identifier.typeRef.isAssignableFrom(identifier.typeRef)
    }

    companion object {

        fun matcherFor(identifier: SingletonDefinition.Identifier<*>): SingletonMatcher {
            return SingletonMatcher(identifier)
        }

    }
}