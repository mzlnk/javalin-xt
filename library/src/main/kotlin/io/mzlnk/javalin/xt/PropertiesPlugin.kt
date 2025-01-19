package io.mzlnk.javalin.xt

import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.properties.internal.management.ApplicationPropertiesFactory
import io.mzlnk.javalin.xt.properties.internal.management.EmptyApplicationProperties
import io.mzlnk.javalin.xt.utils.logger
import java.util.function.Consumer
import kotlin.time.measureTimedValue

internal val io.javalin.config.JavalinConfig.properties: ApplicationProperties
    get() =
        runCatching {
            this.pvt
                .pluginManager.getContextPlugin(PropertiesPlugin::class.java)
                .let { it as PropertiesPlugin }
        }.getOrNull()
            ?.properties
            ?: EmptyApplicationProperties

internal class PropertiesPlugin(
    userConfig: Consumer<ApplicationPropertiesConfig>
) : io.javalin.plugin.ContextPlugin<ApplicationPropertiesConfig, Unit>(
    userConfig,
    ApplicationPropertiesConfig()
) {

    private val log by logger()

    private var _properties: ApplicationProperties? = null

    internal val properties: ApplicationProperties
        get() {
            if (_properties == null) {
                createProperties()
            }
            return _properties ?: throw IllegalStateException("Properties have not been initialized yet.")
        }

    override fun createExtension(context: io.javalin.http.Context) {
        // no-op
    }

    override fun onInitialize(config: io.javalin.config.JavalinConfig) {
        // no-op
    }

    override fun onStart(config: io.javalin.config.JavalinConfig) {
        createProperties()
    }

    private fun createProperties() {
        val (value, elapsedTime) = measureTimedValue {
            val config = ApplicationProperties.Configuration(
                resolveEnvironmentVariables = pluginConfig.resolveEnvironmentVariables,
                profile = pluginConfig.profile
            )
            ApplicationPropertiesFactory().create(config)
        }

        log.info("Application properties created in ${elapsedTime}.")

        _properties = value
    }

}