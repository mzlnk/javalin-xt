package io.mzlnk.javalin.di.internal.definition

import io.mzlnk.javalin.di.ApplicationContext
import java.util.*

internal data class SingletonDefinition(
    val key: Key,
    val source: Source,
    val dependencies: List<Key> = emptyList(),
    val conditions: List<Condition> = emptyList()
) {

    val id: UUID = UUID.randomUUID()

    internal data class Key(
        val type: Type,
        val name: String? = null
    ) {

        override fun toString(): String = "${type}${name?.let { "($it)" } ?: ""}"

    }

    internal data class Source(
        val clazz: Clazz,
        val method: Method
    ) {

        override fun toString(): String = "${clazz}#${method.name}"

    }

    internal interface Condition {

        fun matches(context: ApplicationContext): Boolean

    }

}