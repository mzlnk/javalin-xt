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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is TypeReference<*>) return false

        return type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    internal constructor(type: Type) {
        this.type = type
    }

    internal fun isAssignableFrom(typeRef: TypeReference<*>): Boolean {
        return if (this.type is Class<*> && typeRef.type is Class<*>) {
            this.type.isAssignableFrom(typeRef.type)
        } else {
            typeRef.type == this.type
        }
    }

}