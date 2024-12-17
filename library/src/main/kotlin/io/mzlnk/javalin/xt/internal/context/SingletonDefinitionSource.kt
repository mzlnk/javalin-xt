package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
import java.util.*

/**
 * Source of singleton definitions
 */
internal fun interface SingletonDefinitionSource {

    /**
     * Returns singleton definitions
     *
     * @return singleton definitions
     */
    fun get(): List<SingletonDefinition<*>>

}

/**
 * Default source of singleton definitions.
 * It uses Java SPI [ServiceLoader] to load definitions from the classpath.
 */
internal object DefaultSingletonDefinitionSource : SingletonDefinitionSource {

    override fun get(): List<SingletonDefinition<*>> {
        val providers = ServiceLoader.load(SingletonDefinitionProvider::class.java).toList()
        return providers.flatMap { it.definitions }
    }

}