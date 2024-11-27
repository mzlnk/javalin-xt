package io.mzlnk.javalin.xt.properties

class PropertyNotFoundException(private val key: String) : RuntimeException() {

    override val message: String = "Property with key `$key` not found."

}