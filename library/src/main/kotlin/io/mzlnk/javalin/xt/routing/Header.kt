package io.mzlnk.javalin.xt.routing

/**
 * Marks a parameter as a header.
 *
 * @param name name of header
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Header(val name: String)
