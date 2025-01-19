package io.mzlnk.javalin.xt

import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.context.internal.management.ApplicationContextFactory
import io.mzlnk.javalin.xt.context.internal.management.EmptyApplicationContext
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.utils.logger
import kotlin.time.measureTimedValue

internal val io.javalin.config.JavalinConfig.context: ApplicationContext
    get() =
        runCatching {
            this.pvt
                .pluginManager.getContextPlugin(ContextPlugin::class.java)
                .let { it as ContextPlugin }
        }.getOrNull()
            ?.context
            ?: EmptyApplicationContext


internal class ContextPlugin : io.javalin.plugin.ContextPlugin<Void, Unit>() {

    private val log by logger()

    private lateinit var propertiesProvider: () -> ApplicationProperties
    private var _context: ApplicationContext? = null

    internal val context: ApplicationContext
        get() {
            if (_context == null) {
                createContext()
            }
            return _context ?: throw IllegalStateException("Context has not been initialized yet.")
        }

    override fun createExtension(context: io.javalin.http.Context) {
        // no-op
    }

    override fun onInitialize(config: io.javalin.config.JavalinConfig) {
        propertiesProvider = { config.properties }
    }

    override fun onStart(config: io.javalin.config.JavalinConfig) {
        createContext()
    }

    private fun createContext() {
        val (value, elapsedTime) = measureTimedValue {
            ApplicationContextFactory(propertiesSource = propertiesProvider).create()
        }

        log.info("Application context created in ${elapsedTime}.")

        _context = value
    }

}