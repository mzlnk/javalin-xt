package io.mzlnk.javalin.xt.context

/**
 * Marks a method argument as a property value.
 *
 * @param key the key of the property
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Property(val key: String)
