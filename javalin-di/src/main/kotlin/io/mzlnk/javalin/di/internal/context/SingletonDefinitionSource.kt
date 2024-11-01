package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.definition.SingletonDefinitionProvider
import java.util.*

internal interface SingletonDefinitionSource {

    val definitions: List<SingletonDefinition<*>>

}

internal object DefaultSingletonDefinitionSource : SingletonDefinitionSource {

    override val definitions: List<SingletonDefinition<*>> get() {
        val providers = ServiceLoader.load(SingletonDefinitionProvider::class.java).toList()
        return providers.flatMap { it.definitions }
    }

}