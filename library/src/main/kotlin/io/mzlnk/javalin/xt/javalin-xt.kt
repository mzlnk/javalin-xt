package io.mzlnk.javalin.xt

import io.javalin.Javalin
import io.mzlnk.javalin.xt.di.context.DefaultJavalinContext
import io.mzlnk.javalin.xt.di.context.JavalinContext
import io.mzlnk.javalin.xt.di.context.NoOpJavalinContext
import io.mzlnk.javalin.xt.internal.JavalinXtProxy
import io.mzlnk.javalin.xt.internal.di.context.DefaultSingletonDefinitionSource
import io.mzlnk.javalin.xt.internal.di.context.SingletonDefinitionContext
import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

private val LOG = LoggerFactory.getLogger("io.mzlnk.javalin.xt")

/**
 * Enables javalin-xt features.
 */
fun Javalin.xt(init: JavalinXtConfiguration.() -> Unit = {}): Javalin {
    val config = JavalinXtConfiguration().apply(init)

    val context = if (config.di.enabled) defaultDiContext() else NoOpJavalinContext

    return JavalinXtProxy(
        javalin = this,
        context = context
    )
}

private fun defaultDiContext(): JavalinContext {
    val (context, elapsedTime) = measureTimedValue {
        val definitions = DefaultSingletonDefinitionSource.definitions()
        SingletonDefinitionContext.create(definitions)
    }

    LOG.info("DI context created in $elapsedTime \\o/. Loaded ${context.size()} singletons")

    return DefaultJavalinContext.create(context)
}

/**
 * Returns the context built by javalin-xt DI.
 *
 * @return the context
 */
val Javalin.context: JavalinContext
    get() {
        if (this !is JavalinXtProxy) {
            throw IllegalStateException("This is javalin-xt feature which has not been enabled. Call Javalin.xt() first.")
        }

        return this.context
    }