package io.mzlnk.javalin.xt.context.generated

import io.mzlnk.javalin.xt.context.TypeReference
import io.mzlnk.javalin.xt.context.generated.SingletonDefinition.Identifier
import io.mzlnk.javalin.xt.context.internal.management.elementType
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
     * @property name name of the singleton
     */
    data class Identifier<T : Any>(
        val typeRef: TypeReference<T>,
        val name: String? = null
    ) {

        override fun toString(): String =
            "${typeRef.type.typeName}${name?.let { " ($it)" } ?: ""}"

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
         * @property name name of the singleton
         */
        sealed interface Singleton<T : Any> : DependencyIdentifier<T> {

            val typeRef: TypeReference<T>
            val name: String?


            /**
             * Represents a dependency identifier representing a singleton of singular type.
             *
             * @param T type of the singleton
             *
             * @property typeRef reference to the type of the singleton
             * @property name name of the singleton
             */
            data class Singular<T : Any>(
                override val typeRef: TypeReference<T>,
                override val name: String?
            ) : Singleton<T> {

                override fun toString(): String = "${typeRef.type.typeName}${name?.let { " ($it)" } ?: ""}"
            }


            /**
             * Represents a dependency identifier representing a singleton of list type.
             *
             * @param T type of singleton list element
             *
             * @property typeRef reference to the type of the singleton
             * @property name name of the singleton
             * @property elementName name of the list element
             */
            data class List<T : Any>(
                override val typeRef: TypeReference<kotlin.collections.List<T>>,
                override val name: String?,
                val elementName: String?
            ) : Singleton<kotlin.collections.List<T>> {

                override fun toString(): String =
                    "kotlin.collections.List<${typeRef.elementType.type.typeName}${elementName?.let { "($it)" } ?: ""}>${name?.let { "($it)" } ?: ""}"

            }

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