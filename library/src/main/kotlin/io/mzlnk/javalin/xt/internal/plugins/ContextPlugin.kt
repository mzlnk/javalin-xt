package io.mzlnk.javalin.xt.internal.plugins

import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.internal.context.ApplicationContextFactory
import io.mzlnk.javalin.xt.internal.properties.EmptyApplicationProperties
import io.mzlnk.javalin.xt.internal.utils.logging.logger
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import kotlin.time.measureTimedValue

class ContextPlugin : io.javalin.plugin.ContextPlugin<Void, Unit>() {

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
        propertiesProvider = {
            runCatching {
                config.pvt
                .pluginManager.getContextPlugin(PropertiesPlugin::class.java)
                .let { it as PropertiesPlugin }
            }.getOrNull()
                ?.properties
                ?: EmptyApplicationProperties
        }
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