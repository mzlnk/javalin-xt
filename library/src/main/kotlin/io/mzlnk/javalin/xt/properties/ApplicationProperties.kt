package io.mzlnk.javalin.xt.properties

interface ApplicationProperties {

    operator fun get(key: String): Property = getOrNull(key) ?: throw PropertyNotFoundException(key)
    fun getOrNull(key: String): Property?

}