package io.mzlnk.javalin.xt.internal.common

/**
 * Provides access to environment variables
 */
internal fun interface EnvironmentVariablesProvider {

    /**
     * Gets the value of an environment variable by its key
     *
     * @param key key of the environment variable
     *
     * @return value of the environment variable or null if the variable was not found
     */
    fun get(key: String): String?

}