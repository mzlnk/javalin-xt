package io.mzlnk.javalin.xt.context.definition

/**
 * Represents a provider of singleton definitions.
 *
 * Javalin DI creates a provider implementation that contains all singleton definitions
 * for each module defined using [Module] annotation. It happens during the compilation process
 * using annotation processing.
 *
 */
interface SingletonDefinitionProvider {

    /**
     * List of singleton definitions provided by this provider.
     */
    val definitions: List<SingletonDefinition<*>>

}