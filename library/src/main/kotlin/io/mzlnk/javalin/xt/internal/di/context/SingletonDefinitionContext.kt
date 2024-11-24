package io.mzlnk.javalin.xt.internal.di.context

import io.mzlnk.javalin.xt.di.definition.SingletonDefinition
import io.mzlnk.javalin.xt.internal.di.context.SingletonMatcher.Companion.matcherFor
import io.mzlnk.javalin.xt.di.type.TypeReference

/**
 * Represents a context build by javalin-xt DI based on provided singleton definitions.
 */
internal class SingletonDefinitionContext {

    private val _singletons: MutableList<Pair<SingletonDefinition.Identifier<*>, Any>> = mutableListOf()

    val singletons: List<Pair<SingletonDefinition.Identifier<*>, Any>> get() = _singletons

    /**
     * Returns the number of singletons registered in the context.
     *
     * @return number of singletons
     */
    fun size(): Int = _singletons.size

    /**
     * Register a singleton instance with given identifier in the context.
     *
     * @param identifier identifier of the singleton
     * @param instance instance of the singleton
     */
    fun <T : Any> registerSingleton(identifier: SingletonDefinition.Identifier<out T>, instance: T) {
        _singletons += (identifier to instance)
    }

    /**
     * Find an instance of a singleton with given identifier in the context.
     *
     * Note:
     * If the identifier is of list type, the method will return:
     * - a list of components defined one by one first
     * - a list of components defined explicitly as list if no components defined one by one
     * - an empty list if no components defined
     *
     * @param identifier identifier of the singleton
     *
     * @return instance of the singleton if found, null otherwise
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> findInstance(identifier: SingletonDefinition.Identifier<T>): T? {
        val matcher = matcherFor(identifier)

        val matching = _singletons.filter { (candidateIdentifier, _) ->
            matcher.matches(candidateIdentifier)
        }

        if (identifier.typeRef.isList()) {
            val elementType = (identifier.typeRef as TypeReference<List<Any>>).elementType

            // return components defined one by one first
            matching
                .filter { elementType.isAssignableFrom(it.first.typeRef) }
                .takeIf { it.isNotEmpty() }
                ?.map { it.second }
                ?.let { return it as T }

            // if no components defined one by one, return components defined explicitly as list
            matching
                .filter { identifier.typeRef == it.first.typeRef }
                .takeIf { it.isNotEmpty() }
                ?.also { if (it.size > 1) throw multipleCandidatesFoundException(
                    identifier
                )
                }
                ?.firstOrNull()
                ?.let { return it.second as T }

            // if no components defined, return empty list
            return emptyList<T>() as T
        }

        return matching
            .also { if (matching.size > 1) throw multipleCandidatesFoundException(
                identifier
            )
            }
            .firstOrNull()
            ?.second as? T
    }

    internal companion object {

        /**
         * Create a new context based on provided singleton definitions.
         *
         * It uses a dependency graph to determine the order of singleton creation.
         *
         * @param definitions list of singleton definitions
         *
         * @return new context
         *
         * @throws JavalinDIException if a dependency cycle is found
         * @throws JavalinDIException if no candidates are found for a dependency of a given singleton definition
         */
        fun create(definitions: List<SingletonDefinition<*>>): SingletonDefinitionContext {
            val dependencyGraph = DependencyGraphFactory.create(definitions)

            if (dependencyGraph.hasCycles) {
                throw dependencyCycleFoundException(dependencyGraph.cycles)
            }

            val context = SingletonDefinitionContext()
            dependencyGraph.topologicalOrder.forEach { definition ->
                context.registerSingleton(
                    identifier = definition.identifier,
                    instance = definition.instanceProvider.invoke(
                        definition.dependencies.map { dependency ->
                            context.findInstance(dependency) ?: throw noCandidatesFoundException(
                                dependency
                            )
                        }
                    )
                )
            }

            return context
        }

    }

}