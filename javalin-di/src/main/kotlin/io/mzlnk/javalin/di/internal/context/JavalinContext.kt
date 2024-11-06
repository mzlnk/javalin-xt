package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.context.SingletonMatcher.Companion.matcherFor

internal class JavalinContext {

    private val singletons: MutableList<Pair<SingletonDefinition.Identifier<*>, Any>> = mutableListOf()

    fun size(): Int = singletons.size

    fun <T : Any> registerSingleton(identifier: SingletonDefinition.Identifier<out T>, instance: T) {
        singletons += (identifier to instance)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> findInstance(identifier: SingletonDefinition.Identifier<T>): T? {
        val matcher = matcherFor(identifier)

        val matching = singletons.filter { (candidateIdentifier, _) ->
            matcher.matches(candidateIdentifier)
        }

        if (matching.size > 1) {
            throw multipleCandidatesFoundException(identifier)
        }

        return matching.firstOrNull()?.second as? T
    }

}