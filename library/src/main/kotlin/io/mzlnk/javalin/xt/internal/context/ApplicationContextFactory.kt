package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.JavalinXtConfiguration
import io.mzlnk.javalin.xt.context.ApplicationContext

/**
 * Factory for creating application context
 *
 * @param definitionSource source of singleton definitions
 * @param propertiesSource source of application properties
 */
internal class ApplicationContextFactory(
    private val definitionSource: SingletonDefinitionSource = DefaultSingletonDefinitionSource,
    private val propertiesSource: ApplicationPropertiesSource
) {

    /**
     * Creates application context based on provided configuration
     *
     * @param config configuration of the application
     * @return application context
     */
    fun create(config: JavalinXtConfiguration.Context): ApplicationContext {
        if(!config.enabled) return EmptyApplicationContext

        return DefaultApplicationContext.create(
            definitions = definitionSource.get(),
            properties = propertiesSource.get()
        )
    }

}