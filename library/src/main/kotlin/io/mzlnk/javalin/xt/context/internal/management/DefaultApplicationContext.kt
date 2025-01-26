package io.mzlnk.javalin.xt.context.internal.management

import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.context.TypeReference
import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.context.generated.SingletonDefinition.DependencyIdentifier
import io.mzlnk.javalin.xt.properties.ApplicationProperties

/**
 * Represents a default context build by javalin-xt based on provided singleton definitions.
 */
internal class DefaultApplicationContext : ApplicationContext {

    private val _singletons: MutableList<Pair<SingletonDefinition.Identifier<*>, Any>> = mutableListOf()

    override fun size(): Int = _singletons.size

    override fun <T : Any> findInstance(type: TypeReference<T>, name: String?): T? {
        val singletonToMatch = SingletonToMatch.Singular(
            typeRef = type,
            name = name
        )
        return findInstance(singletonToMatch)
    }

    override fun <T : Any> findInstance(type: TypeReference<List<T>>, name: String?, elementName: String?): List<T>? {
        val singletonToMatch = SingletonToMatch.List(
            typeRef = type,
            name = name,
            elementName = elementName
        )
        return findInstance(singletonToMatch)
    }

    /**
     * Register a singleton instance with given identifier in the context.
     *
     * @param identifier identifier of the singleton
     * @param instance instance of the singleton
     */
    private fun <T : Any> registerSingleton(identifier: SingletonDefinition.Identifier<out T>, instance: T) {
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
    private fun <T : Any> findInstance(singletonToMatch: SingletonToMatch<T>): T? {
        val matching = _singletons.filter { (candidateIdentifier, _) ->
            SingletonMatcher.matches(
                toMatch = singletonToMatch,
                candidate = candidateIdentifier
            )
        }

        if (singletonToMatch is SingletonToMatch.List<*>) {
            val elementType = singletonToMatch.typeRef.elementType

            // return components defined one by one first
            matching
                .filter { elementType.isAssignableFrom(it.first.typeRef) }
                .takeIf { it.isNotEmpty() }
                ?.map { it.second }
                ?.let { return it as T }

            // if no components defined one by one, return components defined explicitly as list
            matching
                .filter { singletonToMatch.typeRef == it.first.typeRef }
                .takeIf { it.isNotEmpty() }
                ?.also { if (it.size > 1) throw multipleCandidatesFoundException(singletonToMatch) }
                ?.firstOrNull()
                ?.let { return it.second as T }

            // if no components defined, return empty list
            return emptyList<T>() as T
        }

        return matching
            .also { if (matching.size > 1) throw multipleCandidatesFoundException(singletonToMatch) }
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
         * @param properties application properties
         *
         * @return new context
         *
         * @throws JavalinDIException if a dependency cycle is found
         * @throws JavalinDIException if no candidates are found for a dependency of a given singleton definition
         */
        fun create(
            definitions: List<SingletonDefinition<*>>,
            properties: ApplicationProperties
        ): DefaultApplicationContext {
            val filteredDefinitions = definitions
                .filter { definition ->
                    definition.conditions.all { it.matches(properties) }
                }

            val dependencyGraph = DependencyGraphFactory.create(filteredDefinitions)

            if (dependencyGraph.hasCycles) {
                throw dependencyCycleFoundException(dependencyGraph.cycles)
            }

            val context = DefaultApplicationContext()
            dependencyGraph.topologicalOrder.forEach { definition ->
                context.registerSingleton(
                    identifier = definition.identifier,
                    instance = definition.instanceProvider.invoke(
                        definition.dependencies.map { dependency ->
                            when (dependency) {
                                is DependencyIdentifier.Singleton<*> -> dependency
                                    .let {
                                        when (dependency) {
                                            is DependencyIdentifier.Singleton.Singular<*> ->
                                                SingletonToMatch.Singular(
                                                    typeRef = dependency.typeRef,
                                                    name = dependency.name
                                                )
                                            is DependencyIdentifier.Singleton.List<*> ->
                                                SingletonToMatch.List(
                                                    typeRef = dependency.typeRef,
                                                    name = dependency.name,
                                                    elementName = dependency.elementName
                                                )
                                        }
                                    }
                                    .let { context.findInstance(it) ?: throw noCandidatesFoundException(it) }

                                is DependencyIdentifier.Property -> {
                                    val property = properties.getOrNull(dependency.key)

                                    if (dependency.required && property == null) {
                                        throw propertyNotFound(
                                            singletonIdentifier = definition.identifier,
                                            dependencyIdentifier = dependency
                                        )
                                    }

                                    property
                                        ?.runCatching { dependency.valueProvider.invoke(this) }
                                        ?.onFailure {
                                            throw invalidPropertyType(
                                                singletonIdentifier = definition.identifier,
                                                dependencyIdentifier = dependency
                                            )
                                        }?.getOrNull()
                                }
                            }

                        }
                    )
                )
            }

            return context
        }

        private fun SingletonDefinition.Condition.matches(properties: ApplicationProperties): Boolean {
            // Currently there is only one implementation of Condition
            this as SingletonDefinition.Condition.OnProperty

            return properties.getOrNull(this.property)?.toString()
                ?.let { it == this.havingValue }
                ?: false
        }

    }

}