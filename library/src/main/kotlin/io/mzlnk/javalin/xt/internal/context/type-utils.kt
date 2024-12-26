package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.context.TypeReference
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType

/**
 * Checks if the type reference represents a list.
 *
 * @return true if the type reference represents a list, false otherwise
 */
internal fun <T : Any> TypeReference<T>.isList(): Boolean {
    return (this.type as? ParameterizedType)?.rawType == List::class.java
}

/**
 * Returns the type reference of the elements of the list represented by the type reference.
 *
 * @return type reference of the elements of the list
 */
internal val <T: Any> TypeReference<out List<T>>.elementType: TypeReference<T>
    get() = this.type
        .let { it as ParameterizedType }
        .actualTypeArguments[0]
        .let { it as WildcardType }
        .upperBounds[0]
        .let { object : TypeReference<T>(it) {} }