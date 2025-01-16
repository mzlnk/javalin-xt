package io.mzlnk.javalin.xt.properties

/**
 * Represents a set of application properties
 */
interface ApplicationProperties {

    /**
     * Retrieves a property by its key
     *
     * @param key the key of the property
     *
     * @return the property if exists, exception is thrown otherwise
     * @throws PropertyNotFoundException if no property found for the specified key
     */
    operator fun get(key: String): Property = getOrNull(key) ?: throw PropertyNotFoundException(key)

    /**
     * Retrieves a property by its key or null if no property found
     *
     * @param key the key of the property
     *
     * @return the property if exists, null otherwise
     */
    fun getOrNull(key: String): Property?

    /**
     * Configuration for application properties
     *
     * @property resolveEnvironmentVariables flag that determines whether to resolve environment variables in property values
     * @property profile the profile to use when resolving properties
     */
    data class Configuration(
        val resolveEnvironmentVariables: Boolean = true,
        val profile: String? = null
    )

}