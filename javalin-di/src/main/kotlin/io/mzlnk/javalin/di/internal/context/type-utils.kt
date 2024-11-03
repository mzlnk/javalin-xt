package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.type.TypeReference
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType

internal fun <T : Any> TypeReference<T>.isList(): Boolean {
    return (this.type as? ParameterizedType)?.rawType == List::class.java
}

internal val <T: Any> TypeReference<List<T>>.elementType: TypeReference<T>
    get() = this.type
        .let { it as ParameterizedType }
        .actualTypeArguments[0]
        .let { it as WildcardType }
        .upperBounds[0]
        .let { object : TypeReference<T>(it) {} }