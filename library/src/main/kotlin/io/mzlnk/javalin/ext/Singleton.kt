package io.mzlnk.javalin.ext

/**
 * Marks a function as a singleton.
 *
 * Singletons are used to define the singletons that will be managed by the DI context.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Singleton
