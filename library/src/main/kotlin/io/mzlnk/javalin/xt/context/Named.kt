package io.mzlnk.javalin.xt.context

/**
 * Associates a singleton/singleton dependency with a name.
 *
 * @param name the name of the dependency
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
annotation class Named(val name: String)
