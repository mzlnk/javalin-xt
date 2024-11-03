package io.mzlnk.javalin.di

import io.javalin.Javalin
import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.JavalinProxy
import io.mzlnk.javalin.di.internal.context.JavalinContextFactory
import io.mzlnk.javalin.di.type.TypeReference
import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

private val LOG = LoggerFactory.getLogger("io.mzlnk.javalin.di")

fun Javalin.enableDI(): Javalin {
    val (context, elapsedTime) = measureTimedValue {
        JavalinContextFactory().create()
    }

    LOG.info("Loaded ${context.size()} singletons")
    LOG.info("Javalin DI enabled in $elapsedTime")

    return JavalinProxy(this, context)
}

fun <T: Any> Javalin.singleton(type: Class<T>): T {
    if (this !is JavalinProxy) {
        throw IllegalStateException("Javalin DI has not been enabled. Call Javalin.enableDI() first.")
    }

    val identifier = SingletonDefinition.Identifier(typeRef = object : TypeReference<T>() {})
    return this.context.getOne(identifier)
}