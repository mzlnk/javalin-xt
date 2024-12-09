package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.JavalinXtConfiguration
import io.mzlnk.javalin.xt.context.ApplicationContext

internal class ApplicationContextFactory(
    private val definitionProvider: SingletonDefinitionProvider = DefaultSingletonDefinitionProvider
) {

    fun create(config: JavalinXtConfiguration.Context): ApplicationContext {
        if(!config.enabled) return NoOpApplicationContext

        return DefaultApplicationContext.create(definitions = definitionProvider.get())
    }

}