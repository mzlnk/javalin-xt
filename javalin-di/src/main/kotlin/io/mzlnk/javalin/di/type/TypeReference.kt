package io.mzlnk.javalin.di.type

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/*
 * Concept taken from com.fasterxml.jackson.core.type.TypeReference
 */
abstract class TypeReference<T> {

    val type: Type

    protected constructor() {
        val superClass = javaClass.genericSuperclass
        if (superClass is Class<*>) {
            throw IllegalArgumentException("Internal error: TypeReference constructed without actual type information")
        }
        this.type = (superClass as ParameterizedType).actualTypeArguments[0]
    }

    internal constructor(type: Type) {
        this.type = type
    }

    internal val isIterable get() = Iterable::class.java.isAssignableFrom(clazz(type))

    internal fun isAssignableFrom(typeRef: TypeReference<*>): Boolean {
        if (this.type is Class<*> && typeRef.type is Class<*>) {
            return this.type.isAssignableFrom(typeRef.type)
        } else {
            return typeRef.type == this.type
        }
    }

    private companion object {

        fun clazz(type: Type): Class<*> {
            return when(type) {
                is Class<*> -> type
                is ParameterizedType -> type.rawType as Class<*>
                else -> throw IllegalStateException("Unsupported type: ${type.typeName}")
            }
        }

    }

}