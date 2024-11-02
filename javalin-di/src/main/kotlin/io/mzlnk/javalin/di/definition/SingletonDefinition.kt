package io.mzlnk.javalin.di.definition

import java.util.*

data class SingletonDefinition<T>(
    val identifier: Identifier<T>,
    val dependencies: List<Identifier<out Any>>,
    val instanceProvider: (args: List<*>) -> T
) where T : Any {

    val id: UUID = UUID.randomUUID()

    override fun toString(): String = identifier.toString()

    sealed interface Identifier<T> {

        data class Single<T>(val type: Class<T>) : Identifier<T> {

            override fun toString(): String = type.name

        }

    }

}