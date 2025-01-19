package io.mzlnk.javalin.xt.properties.internal.management

import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.properties.Property

/**
 * Represents empty application properties
 */
internal object EmptyApplicationProperties : ApplicationProperties {

    override fun getOrNull(key: String): Property? = null

}