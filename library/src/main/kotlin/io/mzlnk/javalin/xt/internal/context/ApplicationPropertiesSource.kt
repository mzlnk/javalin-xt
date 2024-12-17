package io.mzlnk.javalin.xt.internal.context

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

