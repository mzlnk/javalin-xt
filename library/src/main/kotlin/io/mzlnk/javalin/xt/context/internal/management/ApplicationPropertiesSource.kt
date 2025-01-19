package io.mzlnk.javalin.xt.context.internal.management

import io.mzlnk.javalin.xt.properties.ApplicationProperties

/**
 * Source of application properties
 */
internal fun interface ApplicationPropertiesSource {

    /**
     * Returns application properties
     *
     * @return application properties
     */
    fun get(): ApplicationProperties

}

