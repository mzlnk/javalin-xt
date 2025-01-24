package io.mzlnk.javalin.xt.routing

/**
 * Associates a path with given endpoint.
 *
 * @param value HTTP path
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Path(val value: String)