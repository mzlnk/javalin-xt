package io.mzlnk.javalin.xt.internal.context

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
     * Creates application context
     *
     * @return application context
     */
    fun create(): ApplicationContext {
        return DefaultApplicationContext.create(
            definitions = definitionSource.get(),
            properties = propertiesSource.get()
        )
    }

}