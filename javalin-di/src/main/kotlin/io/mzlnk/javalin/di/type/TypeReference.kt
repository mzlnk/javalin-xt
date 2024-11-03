package io.mzlnk.javalin.di.type

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/*
 * Concept taken from com.fasterxml.jackson.core.type.TypeReference
 */
abstract class TypeReference<T> protected constructor() {

    val type: Type

    init {
        val superClass = javaClass.genericSuperclass
        if (superClass is Class<*>) {
            throw IllegalArgumentException("Internal error: TypeReference constructed without actual type information")
        }
        this.type = (superClass as ParameterizedType).actualTypeArguments[0]
    }

    internal val isIterable get() = when(type) {
        is Class<*> -> Iterable::class.java.isAssignableFrom(type)
        is ParameterizedType -> Iterable::class.java.isAssignableFrom(type.rawType as Class<*>)
        else -> false
    }

}