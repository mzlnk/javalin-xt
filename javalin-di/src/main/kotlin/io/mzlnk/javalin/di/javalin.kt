package io.mzlnk.javalin.di

import io.javalin.Javalin
import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.JavalinProxy
import io.mzlnk.javalin.di.internal.context.DefaultSingletonDefinitionSource
import io.mzlnk.javalin.di.internal.context.JavalinContext
import io.mzlnk.javalin.di.type.TypeReference
import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

private val LOG = LoggerFactory.getLogger("io.mzlnk.javalin.di")

// test-1
// test-2

fun Javalin.enableDI(): Javalin {
    val (context, elapsedTime) = measureTimedValue {
        val definitions = DefaultSingletonDefinitionSource.definitions()
        JavalinContext.create(definitions)
    }

    LOG.info("Loaded ${context.size()} singletons")
    LOG.info("Javalin DI enabled in $elapsedTime")

    return JavalinProxy(this, context)
}

fun <T : Any> Javalin.getInstance(type: Class<T>): T = getInstance(object : TypeReference<T>() {})

fun <T : Any> Javalin.getInstance(type: TypeReference<T>): T {
    if (this !is JavalinProxy) {
        throw IllegalStateException("Javalin DI has not been enabled. Call Javalin.enableDI() first.")
    }

    val identifier = SingletonDefinition.Identifier(typeRef = type)
    return this.context.findInstance(identifier) ?: throw IllegalStateException("No instance found for $identifier")
}