package io.mzlnk.javalin.xt.context.definition

import io.mzlnk.javalin.xt.context.TypeReference
import java.util.*

/**
 * Represents a definition of a singleton in the source code.
 *
 * @param T type of the singleton
 *
 * @property identifier identifier of the singleton. See [Identifier]
 * @property dependencies list of identifiers of dependencies of the singleton
 * @property instanceProvider method used to create an instance of the defined singleton
 */
data class SingletonDefinition<T>(
    val identifier: Identifier<T>,
    val dependencies: List<DependencyIdentifier<*>> = emptyList(),
    val conditions: List<Condition> = emptyList(),
    val instanceProvider: (args: List<out Any?>) -> T
) where T : Any {

    val id: UUID = UUID.randomUUID()

    override fun toString(): String = identifier.toString()

    /**
     * Represents an identifier of a singleton.
     *
     * @param T type of the singleton
     *
     * @property typeRef reference to the type of the singleton
     */
    data class Identifier<T : Any>(
        val typeRef: TypeReference<T>
    ) {

        override fun toString(): String = typeRef.type.typeName

    }

    /**
     * Represents an identifier of a singleton dependency.
     */
    sealed interface DependencyIdentifier<T : Any> {

        /**
         * Represents a dependency identifier representing a singleton.
         *
         * @param T type of the singleton
         *
         * @property typeRef reference to the type of the singleton
         */
        data class Singleton<T : Any>(
            val typeRef: TypeReference<T>
        ) : DependencyIdentifier<T> {

            override fun toString(): String = typeRef.type.typeName

        }

        /**
         * Represents a dependency identifier representing a property.
         *
         * @param T type of the property
         *
         * @property key key of the property
         * @property typeRef reference to the type of the property
         */
        data class Property<T : Any>(
            val key: String,
            val valueProvider: (io.mzlnk.javalin.xt.properties.Property) -> T,
            val required: Boolean
        ) : DependencyIdentifier<T>

    }

    /**
     * Represents a condition that must be met in order to create the singleton.
     */
    sealed interface Condition {

        /**
         * Represents a condition that must be met in order to create the singleton
         * where a certain property must be present and have a specific value.
         *
         * @param property name of the property
         * @param havingValue value of the property
         */
        data class OnProperty(
            val property: String,
            val havingValue: String
        ) : Condition

    }

}