package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.definition.SingletonDefinitionProvider
import java.util.*

internal fun interface SingletonDefinitionSource {

    fun definitions(): List<SingletonDefinition<*>>

}

internal object DefaultSingletonDefinitionSource : SingletonDefinitionSource {

    override fun definitions(): List<SingletonDefinition<*>> {
        val providers = ServiceLoader.load(SingletonDefinitionProvider::class.java).toList()
        return providers.flatMap { it.definitions }
    }

}