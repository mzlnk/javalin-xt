package io.mzlnk.javalin.di.definition

import java.util.*

data class SingletonDefinition<T>(
    val identifier: Identifier<T>,
    val dependencies: List<Identifier<*>>,
    val instanceProvider: (args: List<*>) -> T
) where T : Any {

    val id: UUID = UUID.randomUUID()

    override fun toString(): String = identifier.toString()

    sealed interface Identifier<T: Any> {

        data class Single<T: Any>(val type: Class<T>) : Identifier<T> {

            override fun toString(): String = type.name

        }

        data class Iterable<T: Any>(val type: Class<T>) : Identifier<List<T>> {

            override fun toString(): String = "List<${type.name}>"

        }

    }

}