package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.definition.SingletonDefinition
import io.mzlnk.javalin.xt.definition.SingletonDefinitionProvider
import java.util.*

// TODO: refactor it

internal fun interface SingletonDefinitionSource {

    fun definitions(): List<SingletonDefinition<*>>

}

internal object DefaultSingletonDefinitionSource : SingletonDefinitionSource {

    override fun definitions(): List<SingletonDefinition<*>> {
        val providers = ServiceLoader.load(SingletonDefinitionProvider::class.java).toList()
        return providers.flatMap { it.definitions }
    }

}