package io.mzlnk.javalin.xt.internal.properties

import io.mzlnk.javalin.xt.internal.utils.jackson.PathReference
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.properties.Property

/**
 * Default implementation of [ApplicationProperties].
 *
 * When looking for a property, it first checks the profile properties source, then the base properties source.
 *
 * @property baseProperties base properties source (application.yml)
 * @property profileProperties profile properties source (application-<profile>.yml)
 */
internal class DefaultApplicationProperties(
    private val baseProperties: PropertySource,
    private val profileProperties: PropertySource
) : ApplicationProperties {

    override fun getOrNull(key: String): Property? {
        val path = PathReference.create(key)
        return profileProperties.find(path) ?: baseProperties.find(path)
    }

}
