package io.mzlnk.javalin.xt

import io.javalin.Javalin
import io.mzlnk.javalin.xt.di.definition.SingletonDefinition
import io.mzlnk.javalin.xt.internal.JavalinXtProxy
import io.mzlnk.javalin.xt.internal.di.context.DefaultSingletonDefinitionSource
import io.mzlnk.javalin.xt.internal.di.context.JavalinContext
import io.mzlnk.javalin.xt.di.type.TypeReference
import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

private val LOG = LoggerFactory.getLogger("io.mzlnk.javalin.xt")

/**
 * Enables javalin-xt features.
 */
fun Javalin.enableXt(): Javalin {
    val (context, elapsedTime) = measureTimedValue {
        val definitions = DefaultSingletonDefinitionSource.definitions()
        JavalinContext.create(definitions)
    }

    LOG.info("Loaded ${context.size()} singletons")
    LOG.info("Javalin DI enabled in $elapsedTime")

    return JavalinXtProxy(this, context)
}

/**
 * Gets an instance of the specified type from the DI context.
 *
 * @param type the type of the instance to get
 * @return the instance if exists
 *
 * @throws IllegalStateException if the DI context has not been enabled
 * @throws IllegalStateException if no instance found for the specified type
 */
fun <T : Any> Javalin.getInstance(type: Class<T>): T = getInstance(object : TypeReference<T>() {})

/**
 * Gets an instance of the specified type from the DI context.
 *
 * @param type the type of the instance to get
 * @return the instance if exists
 *
 * @throws IllegalStateException if the DI context has not been enabled
 * @throws IllegalStateException if no instance found for the specified type
 */
fun <T : Any> Javalin.getInstance(type: TypeReference<T>): T {
    if (this !is JavalinXtProxy) {
        throw IllegalStateException("Javalin DI has not been enabled. Call Javalin.enableDI() first.")
    }

    val identifier = SingletonDefinition.Identifier(typeRef = type)
    return this.context.findInstance(identifier) ?: throw IllegalStateException("No instance found for $identifier")
}