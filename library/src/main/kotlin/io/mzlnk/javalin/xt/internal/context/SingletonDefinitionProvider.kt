package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
import java.util.*

internal fun interface SingletonDefinitionProvider {

    fun get(): List<SingletonDefinition<*>>

}

internal object DefaultSingletonDefinitionProvider :
    io.mzlnk.javalin.xt.internal.context.SingletonDefinitionProvider {

    override fun get(): List<SingletonDefinition<*>> {
        val providers = ServiceLoader.load(SingletonDefinitionProvider::class.java).toList()
        return providers.flatMap { it.definitions }
    }

}