package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition

internal interface SingletonMatcher {

    fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean

    companion object {

        fun matcherFor(identifier: SingletonDefinition.Identifier<*>): SingletonMatcher {
            return when (identifier) {
                is SingletonDefinition.Identifier.Single<*> -> SingleSingletonMatcher(identifier)
                is SingletonDefinition.Identifier.Iterable<*> -> IterableSingletonMatcher(identifier)
            }
        }

    }
}

private class SingleSingletonMatcher(
    private val identifier: SingletonDefinition.Identifier.Single<*>
) : SingletonMatcher {

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return when (identifier) {
            is SingletonDefinition.Identifier.Single<*> -> this.identifier.type.isAssignableFrom(identifier.type)
            is SingletonDefinition.Identifier.Iterable<*> -> false
        }
    }
}

private class IterableSingletonMatcher(
    private val identifier: SingletonDefinition.Identifier.Iterable<*>
) : SingletonMatcher {

    override fun matches(identifier: SingletonDefinition.Identifier<*>): Boolean {
        return when (identifier) {
            is SingletonDefinition.Identifier.Single<*> -> this.identifier.type.isAssignableFrom(identifier.type)
            is SingletonDefinition.Identifier.Iterable<*> -> throw IllegalStateException("Iterable candidate are not supported")
        }
    }
}