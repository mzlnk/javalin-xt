package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.context.TypeReference

internal object NoOpApplicationContext : ApplicationContext {

    override fun size(): Int  = 0

    override fun <T : Any> findInstance(type: TypeReference<T>): T? = null

}