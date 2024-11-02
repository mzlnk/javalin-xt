package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.context.SingletonMatcher.Companion.matcherFor

internal class JavalinContext {

    private val singletons: MutableList<Pair<SingletonDefinition.Identifier<*>, Any>> = mutableListOf()

    fun size(): Int = singletons.size

    fun <T: Any> registerSingleton(instance: T) {
        val identifier = SingletonDefinition.Identifier.Single(type = instance::class.java)
        singletons += (identifier to instance)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> findSingleton(identifier: SingletonDefinition.Identifier<T>): T? {
        val matcher = matcherFor(identifier)

        val matching = singletons.filter { (candidateIdentifier, _) ->
            matcher.matches(candidateIdentifier)
        }

        if(matching.size > 1) {
            throw MultipleCandidatesFoundException(identifier)
        }

        return matching.firstOrNull()?.second as? T
    }

}

private class MultipleCandidatesFoundException(identifier: SingletonDefinition.Identifier<*>) : JavalinContextException() {

    override val message: String = "Multiple candidates found for $identifier"

}

private class NoCandidatesFoundException(identifier: SingletonDefinition.Identifier<*>) : JavalinContextException() {

    override val message: String = "No candidates found for $identifier"

}