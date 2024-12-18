package io.mzlnk.javalin.xt

import io.javalin.Javalin
import io.mzlnk.javalin.xt.di.context.DefaultJavalinContext
import io.mzlnk.javalin.xt.di.context.JavalinContext
import io.mzlnk.javalin.xt.di.context.NoOpJavalinContext
import io.mzlnk.javalin.xt.internal.JavalinXtProxy
import io.mzlnk.javalin.xt.internal.di.context.DefaultSingletonDefinitionSource
import io.mzlnk.javalin.xt.internal.di.context.SingletonDefinitionContext
import io.mzlnk.javalin.xt.internal.properties.ApplicationPropertiesFactory
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

private val LOG = LoggerFactory.getLogger("io.mzlnk.javalin.xt")

/**
 * Enables javalin-xt features.
 */
fun Javalin.xt(init: JavalinXtConfiguration.() -> Unit = {}): Javalin {
    val config = JavalinXtConfiguration().apply(init)

    val context = config.di
        .takeIf { it.enabled }
        ?.let { defaultDiContext() }
        ?: NoOpJavalinContext

    val properties = ApplicationPropertiesFactory().create(config.properties)

    return JavalinXtProxy(
        javalin = this,
        context = context,
        properties = properties
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

/**
 * Returns the application properties read by javalin-xt from resources.
 *
 * @return the properties
 */
val Javalin.properties: ApplicationProperties
    get() {
        if (this !is JavalinXtProxy) {
            throw IllegalStateException("This is javalin-xt feature which has not been enabled. Call Javalin.xt() first.")
        }

        return this.properties
    }