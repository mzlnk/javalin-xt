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
    fun <T : Any> getOne(identifier: SingletonDefinition.Identifier<T>): T {
        val matcher = matcherFor(identifier)

        val matching = singletons.filter { (candidateIdentifier, _) ->
            matcher.matches(candidateIdentifier)
        }

        if (matching.size > 1) {
            throw MultipleCandidatesFoundException(identifier)
        }

        if (matching.isEmpty()) {
            throw NoCandidatesFoundException(identifier)
        }

        return matching.first().second as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> findAll(identifier: SingletonDefinition.Identifier<T>): List<T> {
        val matcher = matcherFor(identifier)

        return singletons
            .filter { (candidateIdentifier, _) -> matcher.matches(candidateIdentifier) }
            .map { it.second as T }
    }

}

private class MultipleCandidatesFoundException(identifier: SingletonDefinition.Identifier<*>) :
    JavalinContextException() {

    override val message: String = "Multiple candidates found for $identifier"

}

private class NoCandidatesFoundException(identifier: SingletonDefinition.Identifier<*>) : JavalinContextException() {

    override val message: String = "No candidates found for $identifier"

}