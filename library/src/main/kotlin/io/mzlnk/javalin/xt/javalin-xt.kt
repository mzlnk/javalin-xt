package io.mzlnk.javalin.xt

import io.javalin.Javalin
import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.internal.JavalinXtProxy
import io.mzlnk.javalin.xt.internal.context.ApplicationContextFactory
import io.mzlnk.javalin.xt.internal.properties.ApplicationPropertiesFactory
import io.mzlnk.javalin.xt.properties.ApplicationProperties

/**
 * Enables javalin-xt features.
 */
fun Javalin.xt(init: JavalinXtConfiguration.() -> Unit = {}): Javalin {
    val config = JavalinXtConfiguration().apply(init)

    val context = ApplicationContextFactory().create(config.context)
    val properties = ApplicationPropertiesFactory().create(config.properties)

    return JavalinXtProxy(
        javalin = this,
        context = context,
        properties = properties
    )
}

/**
 * Returns the context built by javalin-xt DI.
 *
 * @return the context
 */
val Javalin.context: ApplicationContext
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