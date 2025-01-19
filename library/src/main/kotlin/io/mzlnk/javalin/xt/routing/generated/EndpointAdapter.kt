package io.mzlnk.javalin.xt.routing.generated

import io.mzlnk.javalin.xt.routing.Endpoint

/**
 * Represents an adapter for configuring HTTP endpoints in Javalin instance.
 *
 * javalin-xt creates an adapter implementation for each component that contains HTTP endpoint definitions.
 * It happens during the compilation process using annotation processing.
 *
 */
interface EndpointAdapter {

    /**
     * Applies the adapter to the given Javalin instance.
     */
    fun apply(javalin: io.javalin.Javalin)

    /**
     * Factory for creating instance of [EndpointAdapter] based on [Endpoint] instance.
     */
    interface Factory {

        /**
         * Returns the supported endpoint type for this adapter factory.
         */
        val supportedEndpoint: Class<out Endpoint>

        /**
         * Creates an instance of [EndpointAdapter] using the given [Endpoint] instance.
         *
         * @param endpoint HTTP endpoint instance
         *
         * @return instance of [EndpointAdapter]
         */
        fun create(endpoint: Endpoint): EndpointAdapter

    }

}