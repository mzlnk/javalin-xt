package io.mzlnk.javalin.xt

import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.routing.Endpoint
import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
import java.util.*
import java.util.function.Consumer

/**
 * Configuration for application properties
 *
 * @property resolveEnvironmentVariables flag that determines whether to resolve environment variables in property values
 * @property profile the profile to use when resolving properties
 */
class ApplicationPropertiesConfig {

    @JvmField
    var resolveEnvironmentVariables: Boolean = true

    @JvmField
    var profile: String? = null

}

/**
 * Enables application properties support
 *
 * @param config optional configuration for application properties
 */
fun io.javalin.config.JavalinConfig.enableApplicationProperties(
    config: Consumer<ApplicationPropertiesConfig> = Consumer { }
) {
    this.registerPlugin(PropertiesPlugin(config))
}

/**
 * Enables IoC support
 */
fun io.javalin.config.JavalinConfig.enableIoC() {
    this.registerPlugin(ContextPlugin())
}

/**
 * Registers and endpoint defined in declarative way using javalin-xt annotations to the Javalin instance.
 *
 * @param endpoint instance of the endpoint to register
 */
fun io.javalin.Javalin.registerEndpoint(endpoint: Endpoint) {
    val adapterFactory = ServiceLoader.load(EndpointAdapter.Factory::class.java)
        .find { it.supportedEndpoint == endpoint::class.java }
        ?: throw IllegalStateException("No adapter factory found for endpoint: ${endpoint::class.java}")

    adapterFactory.create(endpoint).apply(this)
}

/**
 * Retrieves the application context associated with the Javalin instance
 *
 * @return application context
 */
val io.javalin.Javalin.context: ApplicationContext get() = this.unsafeConfig().context

/**
 * Retrieves the application properties associated with the Javalin instance
 *
 * @return application properties
 */
val io.javalin.Javalin.properties: ApplicationProperties get() = this.unsafeConfig().properties
