package io.mzlnk.javalin.xt.di.context

import io.mzlnk.javalin.xt.di.type.TypeReference

internal object NoOpJavalinContext : JavalinContext {

    override fun size(): Int  = 0

    override fun <T : Any> findInstance(type: TypeReference<T>): T? = null

}