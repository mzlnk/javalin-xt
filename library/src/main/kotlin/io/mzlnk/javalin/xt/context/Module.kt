package io.mzlnk.javalin.xt.context

/**
 * Marks a class as a module.
 *
 * Modules are used to define the singletons that will be managed by the DI context.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Module
