package io.mzlnk.javalin.di.definition

import io.mzlnk.javalin.di.type.TypeReference
import java.util.*

data class SingletonDefinition<T>(
    val identifier: Identifier<T>,
    val dependencies: List<Identifier<*>>,
    val instanceProvider: (args: List<*>) -> T
) where T : Any {

    val id: UUID = UUID.randomUUID()

    override fun toString(): String = identifier.toString()

    data class Identifier<T : Any>(
        val typeRef: TypeReference<T>
    ) {

        override fun toString(): String = typeRef.type.typeName

    }

}