package io.mzlnk.javalin.xt.routing

/**
 * Marks a parameter as a query parameter.
 *
 * @param name query parameter name
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryParameter(val name: String)
