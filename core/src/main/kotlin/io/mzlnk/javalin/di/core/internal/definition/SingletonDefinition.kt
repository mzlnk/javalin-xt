package io.mzlnk.javalin.di.core.internal.definition

import io.mzlnk.javalin.di.core.internal.ApplicationContext
import java.lang.reflect.Method

internal data class SingletonDefinition(
    val key: Key,
    val source: Source,
    val dependencies: List<Dependency> = emptyList(),
    val conditions: List<Condition> = emptyList()
) {

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

    internal sealed interface Dependency {

        val type: Class<*>
        val name: String?

        data class Single(
            override val type: Class<*>,
            override val name: String? = null
        ) : Dependency {

            override fun toString(): String = "${type.canonicalName}${name?.let { "($it)" } ?: ""}"

        }

        data class Iterable(
            override val type: Class<*>,
            override val name: String? = null,
            val iterableType: Class<*>
        ) : Dependency {

            override fun toString(): String  = "${iterableType.simpleName}<${type.canonicalName}>${name?.let { "($it)" } ?: ""}"

        }

    }

    internal interface Condition {

        fun matches(context: ApplicationContext): Boolean

    }

}