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
    val dependencies: List<Identifier<*>>,
    val instanceProvider: (args: List<out Any>) -> T
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

}