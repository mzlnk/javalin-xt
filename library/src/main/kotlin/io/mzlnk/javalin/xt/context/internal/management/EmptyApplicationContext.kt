package io.mzlnk.javalin.xt.context.internal.management

import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.context.TypeReference

/**
 * Represents an empty application context.
 */
internal object EmptyApplicationContext : ApplicationContext {

    override fun size(): Int = 0

    override fun <T : Any> findInstance(
        type: TypeReference<T>,
        name: String?
    ): T? = null

    override fun <T : Any> findInstance(
        type: TypeReference<List<T>>,
        name: String?,
        elementName: String?
    ): List<T>? = null

}