package io.mzlnk.javalin.xt

import io.javalin.Javalin
import io.mzlnk.javalin.xt.di.context.DefaultJavalinContext
import io.mzlnk.javalin.xt.di.context.JavalinContext
import io.mzlnk.javalin.xt.internal.JavalinXtProxy
import io.mzlnk.javalin.xt.internal.di.context.DefaultSingletonDefinitionSource
import io.mzlnk.javalin.xt.internal.di.context.SingletonDefinitionContext
import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

private val LOG = LoggerFactory.getLogger("io.mzlnk.javalin.xt")

/**
 * Enables javalin-xt features.
 */
fun Javalin.enableXt(): Javalin {
    val (context, elapsedTime) = measureTimedValue {
        val definitions = DefaultSingletonDefinitionSource.definitions()
        SingletonDefinitionContext.create(definitions)
    }

    LOG.info("Loaded ${context.size()} singletons")
    LOG.info("Javalin DI enabled in $elapsedTime")

    return JavalinXtProxy(
        javalin = this,
        context = DefaultJavalinContext.create(context)
    )
}

/**
 * Returns the context built by javalin-xt DI.
 *
 * @return the context
 */
val Javalin.context: JavalinContext
    get() {
        if (this !is JavalinXtProxy) {
            throw IllegalStateException("This is javalin-xt feature which has not been enabled. Call Javalin.enableXt() first.")
        }

        return this.context
    }