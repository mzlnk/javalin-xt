package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.internal.processing.Method
import io.mzlnk.javalin.di.internal.processing.Type
import java.util.*

internal data class SingletonDefinition(
    val key: Key,
    val source: Method,
    val dependencies: List<Key>,
    val conditions: List<Condition>,
) {

    val id: UUID = UUID.randomUUID()

    internal data class Key(
        val type: Type,
        val name: String? = null
    ) {

        override fun toString(): String = "${type}${name?.let { "($it)" } ?: ""}"

    }

}