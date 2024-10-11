package io.mzlnk.javalin.di.core.internal.definition

import io.mzlnk.javalin.di.core.internal.ApplicationContext
import java.lang.reflect.Method
import java.util.UUID

internal data class SingletonDefinition(
    val key: Key,
    val source: Source,
    val dependencies: List<Key> = emptyList(),
    val conditions: List<Condition> = emptyList()
) {

    val id: UUID = UUID.randomUUID()

    internal data class Key(
        val type: Class<*>,
        val name: String? = null
    ) {

        override fun toString(): String = "${type.canonicalName}${name?.let { "($it)" } ?: ""}"

    }

    internal data class Source(
        val clazz: Class<*>,
        val method: Method
    ) {

        override fun toString(): String = "${clazz.canonicalName}#${method.name}"

    }

    internal interface Condition {

        fun matches(context: ApplicationContext): Boolean

    }

}