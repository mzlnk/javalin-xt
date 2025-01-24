package io.mzlnk.javalin.xt.routing

/**
 * Marks a parameter as a body of the request.
 *
 * Supported types:
 * - String
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Body